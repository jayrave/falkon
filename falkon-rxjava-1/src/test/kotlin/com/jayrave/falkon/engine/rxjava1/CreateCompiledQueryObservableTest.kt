package com.jayrave.falkon.engine.rxjava1

import com.jayrave.falkon.engine.*
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import rx.Observable
import rx.internal.util.RxRingBuffer
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler
import java.util.*
import java.util.concurrent.CountDownLatch

class CreateCompiledQueryObservableTest {

    private val engineForTest = EngineForTest()

    @Test
    fun testCompiledQueryIsDeliveredOnSubscription() {
        val countDownLatch = CountDownLatch(1)

        engineForTest
                .createDefaultObservableQuery()
                .subscribe { countDownLatch.countDown() }

        countDownLatch.awaitWithDefaultTimeout()
        assertThat(engineForTest.compiledQueriesFor).hasSize(1)
        assertThat(engineForTest.compiledQueriesFor.first()).isEqualTo(defaultQueryInfo)
    }


    @Test
    fun testCompiledQueryIsDeliveredOnInsert() {
        fireRelatedEventsAndAssertCompiledQueryDelivery(
                engineForTest.createDefaultObservableQuery(), defaultQueryInfo,
                DbEvent.forInsert(tableNameForDefaultQuery),
                listOf(
                        DbEvent.forInsert(tableNameForDefaultQuery),
                        DbEvent.forInsert(tableNameNotInDefaultQuery)
                )
        )
    }


    @Test
    fun testCompiledQueryIsDeliveredOnUpdate() {
        fireRelatedEventsAndAssertCompiledQueryDelivery(
                engineForTest.createDefaultObservableQuery(), defaultQueryInfo,
                DbEvent.forUpdate(tableNameForDefaultQuery),
                listOf(
                        DbEvent.forUpdate(tableNameForDefaultQuery),
                        DbEvent.forUpdate(tableNameNotInDefaultQuery)
                )
        )
    }


    @Test
    fun testCompiledQueryIsDeliveredOnDelete() {
        fireRelatedEventsAndAssertCompiledQueryDelivery(
                engineForTest.createDefaultObservableQuery(), defaultQueryInfo,
                DbEvent.forDelete(tableNameForDefaultQuery),
                listOf(
                        DbEvent.forDelete(tableNameForDefaultQuery),
                        DbEvent.forDelete(tableNameNotInDefaultQuery)
                )
        )
    }


    @Test
    fun testCompiledQueryIsDeliveredOnInsertOrReplace() {
        fireRelatedEventsAndAssertCompiledQueryDelivery(
                engineForTest.createDefaultObservableQuery(), defaultQueryInfo,
                DbEvent.forInsertOrReplace(tableNameForDefaultQuery),
                listOf(
                        DbEvent.forInsertOrReplace(tableNameForDefaultQuery),
                        DbEvent.forInsertOrReplace(tableNameNotInDefaultQuery)
                )
        )
    }


    @Test
    fun testCompiledQueryIsNotDeliveredForUnrelatedEvents() {
        // Setup subscription. Use immediate scheduler to not wait around
        engineForTest.createCompiledQueryObservable(
                listOf(tableNameForDefaultQuery), rawSqlForDefaultQuery,
                null, Schedulers.immediate()
        ).subscribe { it.close() }

        // Make sure compiled query was delivered on subscription
        assertThat(engineForTest.compiledQueriesFor).hasSize(1)

        // Fire unrelated events
        engineForTest.fireEvent(DbEvent.forInsert(tableNameNotInDefaultQuery))
        engineForTest.fireEvents(listOf(
                DbEvent.forUpdate(tableNameNotInDefaultQuery),
                DbEvent.forDelete(tableNameNotInDefaultQuery),
                DbEvent.forInsertOrReplace(tableNameNotInDefaultQuery)
        ))

        // Make sure compiled query wasn't delivered for unrelated subscription
        assertThat(engineForTest.compiledQueriesFor).hasSize(1)
        assertThat(engineForTest.compiledQueriesFor.first()).isEqualTo(defaultQueryInfo)
    }


    @Test
    fun testBackpressureSupportedWhenSchedulerSlow() {
        val testScheduler = TestScheduler()
        val testSubscriber = TestSubscriber<CompiledStatement<Source>>()

        engineForTest.createCompiledQueryObservable(
                listOf(tableNameForDefaultQuery), rawSqlForDefaultQuery, null, testScheduler
        ).subscribe(testSubscriber)

        // Fire more events than the scheduler queue can handle
        (1..RxRingBuffer.SIZE * 2).forEach {
            engineForTest.fireEvent(DbEvent.forInsert(tableNameForDefaultQuery))
        }

        testScheduler.triggerActions()

        // Assert we got all the events from the queue plus the one buffered from backpressure
        assertThat(testSubscriber.onNextEvents).hasSize(RxRingBuffer.SIZE + 1)
    }


    /**
     * Both [event] & [events] should deal with the tables [compiledQueryObservable]
     * is related with
     */
    private fun fireRelatedEventsAndAssertCompiledQueryDelivery(
            compiledQueryObservable: Observable<CompiledStatement<Source>>,
            expectedQueryInfo: QueryInfo, event: DbEvent, events: List<DbEvent>) {

        // Setup count down latches
        val subscriptionCountDownLatch = CountDownLatch(1)
        val eventCountDownLatch = CountDownLatch(1)
        val eventListCountDownLatch = CountDownLatch(1)
        val latchCountDownSerializer = LatchCountDownSerializer(
                subscriptionCountDownLatch, eventCountDownLatch, eventListCountDownLatch
        )

        // Subscribe to the given observable
        compiledQueryObservable.subscribe {
            it.close()
            latchCountDownSerializer.countDown()
        }

        // Wait for compiled query delivery on subscription
        subscriptionCountDownLatch.awaitWithDefaultTimeout()
        assertThat(engineForTest.compiledQueriesFor).hasSize(1)

        // Fire event & make sure that compiled query is delivered
        engineForTest.fireEvent(event)
        eventCountDownLatch.awaitWithDefaultTimeout()
        assertThat(engineForTest.compiledQueriesFor).hasSize(2)

        // Fire event list & make sure that compiled query is delivered
        engineForTest.fireEvents(events)
        eventListCountDownLatch.awaitWithDefaultTimeout()
        assertThat(engineForTest.compiledQueriesFor).hasSize(3)

        // Assert that the expected compiled query was delivered every time
        engineForTest.compiledQueriesFor.forEach { assertThat(it).isEqualTo(expectedQueryInfo) }
    }



    private class EngineForTest : Engine {

        val compiledQueriesFor = ArrayList<QueryInfo>()
        private val dbEventListeners = ArrayList<DbEventListener>()

        override fun <R> executeInTransaction(operation: () -> R) = throw exception()
        override fun isInTransaction() = throw exception()
        override fun compileSql(tableNames: Iterable<String>?, rawSql: String) = throw exception()
        override fun compileInsert(tableName: String, rawSql: String) = throw exception()
        override fun compileUpdate(tableName: String, rawSql: String) = throw exception()
        override fun compileDelete(tableName: String, rawSql: String) = throw exception()
        private fun exception() = UnsupportedOperationException("not implemented")

        override fun compileQuery(tableNames: Iterable<String>, rawSql: String):
                CompiledStatement<Source> {

            compiledQueriesFor.add(QueryInfo(tableNames, rawSql))
            return mock()
        }

        override fun registerDbEventListener(dbEventListener: DbEventListener) {
            dbEventListeners.add(dbEventListener)
        }

        override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
            dbEventListeners.remove(dbEventListener)
        }

        fun fireEvent(event: DbEvent) = dbEventListeners.forEach { it.onEvent(event) }
        fun fireEvents(events: List<DbEvent>) = dbEventListeners.forEach { it.onEvents(events) }
    }



    private data class QueryInfo(val tableNames: Iterable<String>, val rawSql: String)



    companion object {

        private const val tableNameForDefaultQuery = "table in default query"
        private const val tableNameNotInDefaultQuery = "table not in default query"
        private const val rawSqlForDefaultQuery = "this is an example raw sql"
        private val defaultQueryInfo = QueryInfo(
                listOf(tableNameForDefaultQuery), rawSqlForDefaultQuery
        )

        private fun Engine.createDefaultObservableQuery(arguments: Iterable<Any>? = null):
                Observable<CompiledStatement<Source>> {

            return createCompiledQueryObservable(
                    listOf(tableNameForDefaultQuery), rawSqlForDefaultQuery,
                    arguments, Schedulers.newThread()
            )
        }
    }
}