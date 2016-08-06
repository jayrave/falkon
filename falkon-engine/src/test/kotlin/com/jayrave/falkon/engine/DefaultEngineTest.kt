package com.jayrave.falkon.engine

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



    companion object {
        private const val DUMMY_SQL = "dummy_sql"
        private const val DUMMY_TABLE_NAME = "dummy_table_name"

        private fun buildEngineCoreForTesting():EngineCoreForTestingEngine {
            return EngineCoreForTestingEngine.createWithCompiledStatementsForTest()
        }
    }
}