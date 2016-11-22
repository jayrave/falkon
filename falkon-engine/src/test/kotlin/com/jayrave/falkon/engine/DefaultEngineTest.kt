package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.testLib.DbEventListenerForTest
import com.jayrave.falkon.engine.testLib.StoringLogger
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DefaultEngineTest {

    @Test
    fun testCanExecuteNestedTransactions() {
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        var numberOfTransactionsExecuted = 0
        defaultEngine.executeInTransaction {
            defaultEngine.executeInTransaction {
                defaultEngine.executeInTransaction {
                    numberOfTransactionsExecuted++
                }
                numberOfTransactionsExecuted++
            }
            numberOfTransactionsExecuted++
        }

        assertThat(numberOfTransactionsExecuted).isEqualTo(3)
    }


    @Test
    fun testTransactionNestingIsNotDelegatedToEngineCore() {
        val engineCore = buildEngineCoreForTesting()
        val defaultEngine = DefaultEngine(engineCore)
        defaultEngine.executeInTransaction {
            defaultEngine.executeInTransaction {
                defaultEngine.executeInTransaction {}
            }
        }

        assertThat(engineCore.numberOfTransactionsReceived).isEqualTo(1)
    }


    @Test
    fun testOnlyOneCommitOnSuccessNoMatterTheLevelOfTransactionNesting() {
        val engineCore = buildEngineCoreForTesting()
        val defaultEngine = DefaultEngine(engineCore)
        defaultEngine.executeInTransaction {
            assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0)
            defaultEngine.executeInTransaction {
                assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0)
                defaultEngine.executeInTransaction {
                    assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0)
                }
                assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0)
            }
            assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0)
        }

        assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(1)
    }


    @Test
    fun testAllChangesAreRolledBackIfOuterMostTransactionFails() {
        val engineCore = buildEngineCoreForTesting()
        val defaultEngine = DefaultEngine(engineCore)
        val exceptionCaught: Boolean

        try {
            defaultEngine.executeInTransaction {
                defaultEngine.executeInTransaction {
                    defaultEngine.executeInTransaction {}
                }

                throw RuntimeException()
            }
        } catch (e: Exception) {
           exceptionCaught = true
        }

        assertThat(exceptionCaught).isTrue()
        assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0) // Nothing must have been committed
        assertThat(engineCore.numberOfTransactionsRolledBack).isEqualTo(1) // There must be only one rollback
    }


    @Test
    fun testAllChangesAreRolledBackIfAnyInnerTransactionFails() {
        val engineCore = buildEngineCoreForTesting()
        val defaultEngine = DefaultEngine(engineCore)
        val exceptionCaught: Boolean

        try {
            defaultEngine.executeInTransaction {
                defaultEngine.executeInTransaction {
                    defaultEngine.executeInTransaction {
                        throw RuntimeException()
                    }
                }
            }
        } catch (e: Exception) {
            exceptionCaught = true
        }

        assertThat(exceptionCaught).isTrue()
        assertThat(engineCore.numberOfTransactionsCommitted).isEqualTo(0) // Nothing must have been committed
        assertThat(engineCore.numberOfTransactionsRolledBack).isEqualTo(1) // There must be only one rollback
    }


    @Test
    fun testIsInTransactionReturnsAppropriateFlag() {
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())

        // Outside transaction
        assertThat(defaultEngine.isInTransaction()).isFalse()

        // Top level transaction
        defaultEngine.executeInTransaction {
            assertThat(defaultEngine.isInTransaction()).isTrue()
            defaultEngine.compileInsert(DUMMY_TABLE_NAME, DUMMY_SQL).execute()
            assertThat(defaultEngine.isInTransaction()).isTrue()
        }

        // Outside transaction
        assertThat(defaultEngine.isInTransaction()).isFalse()
    }


    @Test
    fun testIsInTransactionReturnsAppropriateFlagForNestedTransactions() {
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())

        // Outside transaction
        assertThat(defaultEngine.isInTransaction()).isFalse()

        // Top level transaction
        defaultEngine.executeInTransaction {
            assertThat(defaultEngine.isInTransaction()).isTrue()
            defaultEngine.compileUpdate(DUMMY_TABLE_NAME, DUMMY_SQL).execute()

            // First level nested transaction
            defaultEngine.executeInTransaction {
                assertThat(defaultEngine.isInTransaction()).isTrue()
                defaultEngine.compileDelete(DUMMY_TABLE_NAME, DUMMY_SQL).execute()

                // Second level nested transaction
                defaultEngine.executeInTransaction {
                    assertThat(defaultEngine.isInTransaction()).isTrue()
                    defaultEngine.compileQuery(emptyList(), DUMMY_SQL).execute()
                }

                assertThat(defaultEngine.isInTransaction()).isTrue()
                defaultEngine.compileSql(emptyList(), DUMMY_SQL).execute()
            }

            assertThat(defaultEngine.isInTransaction()).isTrue()
            defaultEngine.compileUpdate(DUMMY_TABLE_NAME, DUMMY_SQL).execute()
        }

        // Outside transaction
        assertThat(defaultEngine.isInTransaction()).isFalse()
    }


    @Test
    fun testEventFiredOutsideTransactionIsDelivered() {
        val eventListener = DbEventListenerForTest()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        defaultEngine.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.singleEvents).isEmpty()

        // Execute insert
        val tableName = "example table"
        defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()

        // Assert event is delivered
        assertThat(eventListener.singleEvents).containsOnly(DbEvent.forInsert(tableName))
        assertThat(eventListener.multiEventsList).isEmpty()
    }


    @Test
    fun testEventsFiredInsideTransactionAreDeliveredOnCommit() {
        val eventListener = DbEventListenerForTest()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        defaultEngine.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.multiEventsList).isEmpty()

        // Execute insert, update & delete inside transaction
        val tableName = "example table"
        defaultEngine.executeInTransaction {
            defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()
            defaultEngine.compileUpdate(tableName, DUMMY_SQL).execute()
            defaultEngine.compileDelete(tableName, DUMMY_SQL).execute()
            defaultEngine.compileInsertOrReplace(tableName, DUMMY_SQL).execute()

            // Assert events are not delivered yet
            assertThat(eventListener.multiEventsList).isEmpty()
        }

        // Assert events are delivered
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).hasSize(1)
        assertThat(eventListener.multiEventsList.first()).containsOnly(
                DbEvent.forInsert(tableName),
                DbEvent.forUpdate(tableName),
                DbEvent.forDelete(tableName),
                DbEvent.forInsertOrReplace(tableName)
        )
    }


    @Test
    fun testEventsFiredInsideTransactionAreDiscardedOnRollback() {
        val eventListener = DbEventListenerForTest()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        defaultEngine.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.multiEventsList).isEmpty()

        // Execute insert, update & delete inside transaction
        val exceptionWasCaught: Boolean
        try {
            val tableName = "example table 1"
            defaultEngine.executeInTransaction {
                defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()
                defaultEngine.compileUpdate(tableName, DUMMY_SQL).execute()
                defaultEngine.compileDelete(tableName, DUMMY_SQL).execute()
                defaultEngine.compileInsertOrReplace(tableName, DUMMY_SQL).execute()

                throw Exception("just for testing")
            }
        } catch (e: Exception) {
            exceptionWasCaught = true
        }

        // Assert exception was thrown & events weren't delivered
        assertThat(exceptionWasCaught).isTrue()
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).isEmpty()

        // Execute insert
        val tableName = "example table 2"
        defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()

        // Assert only appropriate event is delivered
        assertThat(eventListener.singleEvents).containsOnly(DbEvent.forInsert(tableName))
        assertThat(eventListener.multiEventsList).isEmpty()
    }


    @Test
    fun testEventsFiredOutsideTransactionsAreNotDeliveredToUnregisteredListeners() {
        val eventListener = DbEventListenerForTest()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        defaultEngine.registerDbEventListener(eventListener)
        defaultEngine.unregisterDbEventListener(eventListener)

        // Execute insert
        val tableName = "example table"
        defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()

        // Assert no events are delivered to unregistered listener
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).isEmpty()
    }


    @Test
    fun testEventsFiredInsideTransactionsAreNotDeliveredToUnregisteredListeners() {
        val eventListener1 = DbEventListenerForTest()
        val eventListener2 = DbEventListenerForTest()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        defaultEngine.registerDbEventListener(eventListener1)
        defaultEngine.registerDbEventListener(eventListener2)

        // Unregister the first event listener
        defaultEngine.unregisterDbEventListener(eventListener1)

        // Execute insert
        defaultEngine.executeInTransaction {
            val tableName = "example table"
            defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()

            // Unregister the second event listener too
            defaultEngine.unregisterDbEventListener(eventListener2)
        }

        // Assert no events are delivered to unregistered listeners
        assertThat(eventListener1.singleEvents).isEmpty()
        assertThat(eventListener1.multiEventsList).isEmpty()
        assertThat(eventListener2.singleEvents).isEmpty()
        assertThat(eventListener2.multiEventsList).isEmpty()
    }


    @Test
    fun testAllKindsOfDbEventsAreDelivered() {
        val eventListener = DbEventListenerForTest()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting())
        defaultEngine.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.singleEvents).isEmpty()

        // Execute insert, update & delete
        val tableName = "example table"
        defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()
        defaultEngine.compileUpdate(tableName, DUMMY_SQL).execute()
        defaultEngine.compileDelete(tableName, DUMMY_SQL).execute()
        defaultEngine.compileInsertOrReplace(tableName, DUMMY_SQL).execute()

        // Assert event is delivered
        assertThat(eventListener.multiEventsList).isEmpty()
        assertThat(eventListener.singleEvents).containsExactly(
                DbEvent.forInsert(tableName),
                DbEvent.forUpdate(tableName),
                DbEvent.forDelete(tableName),
                DbEvent.forInsertOrReplace(tableName)
        )
    }


    @Test
    fun testLoggerIsInformedOnSuccessfulExecutions() {
        val storingLogger = StoringLogger()
        val defaultEngine = DefaultEngine(buildEngineCoreForTesting(), storingLogger)

        val sqlForCompileSql = "SQL for compile sql"
        defaultEngine.compileSql(null, sqlForCompileSql).execute()
        storingLogger.assertStoredLogInfo(true, sqlForCompileSql)

        val dummyTableName = "dummy table"
        val firstArgIndex = 1

        val sqlForCompileInsert = "SQL for compile insert"
        val argForCompileInsert = "insert"
        defaultEngine
                .compileInsert(dummyTableName, sqlForCompileInsert)
                .bindString(firstArgIndex, argForCompileInsert)
                .execute()
        storingLogger.assertStoredLogInfo(true, sqlForCompileInsert, argForCompileInsert)

        val sqlForCompileUpdate = "SQL for compile update"
        val argForCompileUpdate = "update"
        defaultEngine
                .compileUpdate(dummyTableName, sqlForCompileUpdate)
                .bindString(firstArgIndex, argForCompileUpdate)
                .execute()
        storingLogger.assertStoredLogInfo(true, sqlForCompileUpdate, argForCompileUpdate)

        val sqlForCompileDelete = "SQL for compile delete"
        val argForCompileDelete = "delete"
        defaultEngine
                .compileDelete(dummyTableName, sqlForCompileDelete)
                .bindString(firstArgIndex, argForCompileDelete)
                .execute()
        storingLogger.assertStoredLogInfo(true, sqlForCompileDelete, argForCompileDelete)

        val sqlForCompileInsertOrReplace = "SQL for compile insert or replace"
        val argForCompileInsertOrReaplce = "insert or replace"
        defaultEngine
                .compileInsertOrReplace(dummyTableName, sqlForCompileInsertOrReplace)
                .bindString(firstArgIndex, argForCompileInsertOrReaplce)
                .execute()
        storingLogger.assertStoredLogInfo(
                true, sqlForCompileInsertOrReplace, argForCompileInsertOrReaplce
        )

        val sqlForCompileQuery = "SQL for compile query"
        val argForCompileQuery = "query"
        defaultEngine
                .compileQuery(listOf(dummyTableName), sqlForCompileQuery)
                .bindString(firstArgIndex, argForCompileQuery)
                .execute()
        storingLogger.assertStoredLogInfo(true, sqlForCompileQuery, argForCompileQuery)
    }


    @Test
    fun testLoggerIsInformedOnExecutionFailure() {
        // All compiled statements from this engine core would throw on execution
        val engineCoreForTesting = EngineCoreForTestingEngine.createWithCompiledStatementsForTest(
                sqlProvider = { sql -> UnitReturningCompiledStatementForTest(sql, true) },
                insertProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1, true) },
                updateProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1, true) },
                deleteProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1, true) },
                insertOrReplaceProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1, true) },
                queryProvider = { sql -> CompiledStatementForQueryForTest(sql, mock(), true) }
        )

        val storingLogger = StoringLogger()
        val defaultEngine = DefaultEngine(engineCoreForTesting, storingLogger)

        val dummyTableName = "dummy table"
        val firstArgIndex = 1
        var numberOfExceptionsCaught = 0

        val sqlForCompileSql = "SQL for compile sql"
        try {
            defaultEngine.compileSql(null, sqlForCompileSql).execute()
        } catch (e: Exception) {
            storingLogger.assertStoredLogInfo(false, sqlForCompileSql)
            numberOfExceptionsCaught++
        }

        val sqlForCompileInsert = "SQL for compile insert"
        val argForCompileInsert = "insert"
        try {
            defaultEngine
                    .compileInsert(dummyTableName, sqlForCompileInsert)
                    .bindString(firstArgIndex, argForCompileInsert)
                    .execute()

        } catch (e: Exception) {
            storingLogger.assertStoredLogInfo(false, sqlForCompileInsert, argForCompileInsert)
            numberOfExceptionsCaught++
        }

        val sqlForCompileUpdate = "SQL for compile update"
        val argForCompileUpdate = "update"
        try {
            defaultEngine
                    .compileUpdate(dummyTableName, sqlForCompileUpdate)
                    .bindString(firstArgIndex, argForCompileUpdate)
                    .execute()
        } catch (e: Exception) {
            storingLogger.assertStoredLogInfo(false, sqlForCompileUpdate, argForCompileUpdate)
            numberOfExceptionsCaught++
        }

        val sqlForCompileDelete = "SQL for compile delete"
        val argForCompileDelete = "delete"
        try {
            defaultEngine
                    .compileDelete(dummyTableName, sqlForCompileDelete)
                    .bindString(firstArgIndex, argForCompileDelete)
                    .execute()
        } catch (e: Exception) {
            storingLogger.assertStoredLogInfo(false, sqlForCompileDelete, argForCompileDelete)
            numberOfExceptionsCaught++
        }

        val sqlForCompileInsertOrReplace = "SQL for compile insert or replace"
        val argForCompileInsertOrReplace = "insert or replace"
        try {
            defaultEngine
                    .compileInsertOrReplace(dummyTableName, sqlForCompileInsertOrReplace)
                    .bindString(firstArgIndex, argForCompileInsertOrReplace)
                    .execute()
        } catch (e: Exception) {
            numberOfExceptionsCaught++
            storingLogger.assertStoredLogInfo(
                    false, sqlForCompileInsertOrReplace, argForCompileInsertOrReplace
            )
        }

        val sqlForCompileQuery = "SQL for compile query"
        val argForCompileQuery = "query"
        try {
            defaultEngine
                    .compileQuery(listOf(dummyTableName), sqlForCompileQuery)
                    .bindString(firstArgIndex, argForCompileQuery)
                    .execute()
        } catch (e: Exception) {
            storingLogger.assertStoredLogInfo(false, sqlForCompileQuery, argForCompileQuery)
            numberOfExceptionsCaught++
        }

        assertThat(numberOfExceptionsCaught).isEqualTo(6)
    }



    companion object {
        private const val DUMMY_SQL = "dummy_sql"
        private const val DUMMY_TABLE_NAME = "dummy_table_name"

        private fun buildEngineCoreForTesting():EngineCoreForTestingEngine {
            return EngineCoreForTestingEngine.createWithCompiledStatementsForTest(
                    insertProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1) },
                    updateProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1) },
                    deleteProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1) },
                    insertOrReplaceProvider = { sql -> IntReturningCompiledStatementForTest(sql, 1) }
            )
        }

        private fun StoringLogger.assertStoredLogInfo(
                success: Boolean, sql: String, vararg args: Any?) {

            val logInfo = when (success) {
                true -> onSuccessfulExecution
                else -> onExecutionFailed
            }

            assertThat(logInfo?.sql).isEqualTo(sql)
            assertThat(logInfo?.arguments).containsExactly(*args)
        }
    }
}