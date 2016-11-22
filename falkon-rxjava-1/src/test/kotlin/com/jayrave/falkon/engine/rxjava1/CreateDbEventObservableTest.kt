package com.jayrave.falkon.engine.rxjava1

import com.jayrave.falkon.engine.DbEvent
import com.jayrave.falkon.engine.DbEventListener
import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch

class CreateDbEventObservableTest {

    private val engineForTest = EngineForTest()

    @Test
    fun testInsertDbEventIsDelivered() {
        val insertEventToBeFired = DbEvent.forInsert("test")
        val caughtEvents = fireSingleEventAndCatchResult(insertEventToBeFired)
        assertThat(caughtEvents).hasSize(1)
        assertThat(caughtEvents.first()).isEqualTo(insertEventToBeFired)
    }


    @Test
    fun testUpdateDbEventIsDelivered() {
        val updateEventToBeFired = DbEvent.forUpdate("test")
        val caughtEvents = fireSingleEventAndCatchResult(updateEventToBeFired)
        assertThat(caughtEvents).hasSize(1)
        assertThat(caughtEvents.first()).isEqualTo(updateEventToBeFired)
    }


    @Test
    fun testDeleteDbEventIsDelivered() {
        val deleteEventToBeFired = DbEvent.forDelete("test")
        val caughtEvents = fireSingleEventAndCatchResult(deleteEventToBeFired)
        assertThat(caughtEvents).hasSize(1)
        assertThat(caughtEvents.first()).isEqualTo(deleteEventToBeFired)
    }


    @Test
    fun testInsertOrReplaceDbEventIsDelivered() {
        val insertOrReplaceEventToBeFired = DbEvent.forInsertOrReplace("test")
        val caughtEvents = fireSingleEventAndCatchResult(insertOrReplaceEventToBeFired)
        assertThat(caughtEvents).hasSize(1)
        assertThat(caughtEvents.first()).isEqualTo(insertOrReplaceEventToBeFired)
    }


    @Test
    fun testAllEventsAreDeliveredWhenMultipleEventsAreFiredIndividually() {
        val eventsToBeFired = listOf(
                DbEvent.forInsert("test_1"),
                DbEvent.forUpdate("test_2"),
                DbEvent.forDelete("test_3"),
                DbEvent.forInsertOrReplace("test_4")
        )

        val countDownLatch = CountDownLatch(eventsToBeFired.count())
        val caughtEvents = ArrayList<DbEvent>()
        engineForTest.createDbEventObservable().subscribe {
            caughtEvents.addAll(it)
            countDownLatch.countDown()
        }

        // Fire events
        eventsToBeFired.forEach { event ->
            engineForTest.dbEventListeners.forEach { eventListener ->
                eventListener.onEvent(event)
            }
        }

        // Wait for events to be caught
        countDownLatch.awaitWithDefaultTimeout()

        // Assert caught events
        assertThat(caughtEvents).hasSameElementsAs(eventsToBeFired)
    }


    @Test
    fun testAllEventsAreDeliveredWhenMultipleEventsAreFiredTogether() {
        val eventsToBeFired = listOf(
                DbEvent.forInsert("test_1"),
                DbEvent.forUpdate("test_2"),
                DbEvent.forDelete("test_3"),
                DbEvent.forInsertOrReplace("test_4")
        )

        val countDownLatch = CountDownLatch(1)
        val caughtEvents = ArrayList<DbEvent>()
        engineForTest.createDbEventObservable().subscribe {
            caughtEvents.addAll(it)
            countDownLatch.countDown()
        }

        engineForTest.dbEventListeners.forEach { it.onEvents(eventsToBeFired) } // Fire events
        countDownLatch.awaitWithDefaultTimeout() // Wait for events to be caught

        // Assert caught events
        assertThat(caughtEvents).hasSameElementsAs(eventsToBeFired)
    }


    @Test
    fun testDbEventListenerIsUnregisteredOnUnsubscription() {
        assertThat(engineForTest.dbEventListeners).isEmpty()
        val subscription = engineForTest.createDbEventObservable().subscribe()
        assertThat(engineForTest.dbEventListeners).hasSize(1)
        subscription.unsubscribe()
        assertThat(engineForTest.dbEventListeners).isEmpty()
    }


    private fun fireSingleEventAndCatchResult(eventToBeFired: DbEvent): List<DbEvent> {
        val countDownLatch = CountDownLatch(1)
        val caughtEvents = ArrayList<DbEvent>()
        engineForTest.createDbEventObservable().subscribe {
            caughtEvents.addAll(it)
            countDownLatch.countDown()
        }

        engineForTest.dbEventListeners.forEach { it.onEvent(eventToBeFired) } // Fire event
        countDownLatch.awaitWithDefaultTimeout() // Wait for event to be caught
        return caughtEvents // Send back caught events
    }



    private class EngineForTest : Engine {

        val dbEventListeners = ArrayList<DbEventListener>()

        override fun <R> executeInTransaction(operation: () -> R) = throw exception()
        override fun isInTransaction() = throw exception()
        override fun compileSql(tableNames: Iterable<String>?, rawSql: String) = throw exception()
        override fun compileInsert(tableName: String, rawSql: String) = throw exception()
        override fun compileUpdate(tableName: String, rawSql: String) = throw exception()
        override fun compileDelete(tableName: String, rawSql: String) = throw exception()
        override fun compileInsertOrReplace(tableName: String, rawSql: String) = throw exception()
        override fun compileQuery(tableNames: Iterable<String>, rawSql: String) = throw exception()
        private fun exception() = UnsupportedOperationException("not implemented")

        override fun registerDbEventListener(dbEventListener: DbEventListener) {
            dbEventListeners.add(dbEventListener)
        }

        override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
            dbEventListeners.remove(dbEventListener)
        }
    }
}