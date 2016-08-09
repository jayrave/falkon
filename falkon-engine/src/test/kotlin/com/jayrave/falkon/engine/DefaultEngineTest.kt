package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.testLib.DbEventListenerForTest
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
        var exceptionCaught = false

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
        var exceptionCaught = false

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

            // Assert events are not delivered yet
            assertThat(eventListener.multiEventsList).isEmpty()
        }

        // Assert events are delivered
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).hasSize(1)
        assertThat(eventListener.multiEventsList.first()).containsOnly(
                DbEvent.forInsert(tableName),
                DbEvent.forUpdate(tableName),
                DbEvent.forDelete(tableName)
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
        var exceptionWasCaught = false
        try {
            val tableName = "example table 1"
            defaultEngine.executeInTransaction {
                defaultEngine.compileInsert(tableName, DUMMY_SQL).execute()
                defaultEngine.compileUpdate(tableName, DUMMY_SQL).execute()
                defaultEngine.compileDelete(tableName, DUMMY_SQL).execute()

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



    companion object {
        private const val DUMMY_SQL = "dummy_sql"
        private const val DUMMY_TABLE_NAME = "dummy_table_name"

        private fun buildEngineCoreForTesting():EngineCoreForTestingEngine {
            return EngineCoreForTestingEngine.createWithCompiledStatementsForTest(
                    insertProvider = { sql -> CompiledInsertForTest(sql, 1) },
                    updateProvider = { sql -> CompiledUpdateForTest(sql, 1) },
                    deleteProvider = { sql -> CompiledDeleteForTest(sql, 1) }
            )
        }
    }
}