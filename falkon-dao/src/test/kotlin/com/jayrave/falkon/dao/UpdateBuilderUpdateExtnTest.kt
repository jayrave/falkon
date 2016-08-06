package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledUpdateForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.dao.update.UpdateBuilderImpl
import com.jayrave.falkon.dao.update.testLib.UpdateSqlBuilderForTesting
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class UpdateBuilderUpdateExtnTest {

    private val argPlaceholder = "?"
    private val updateSqlBuilder = UpdateSqlBuilderForTesting()

    @Test
    fun testUpdateViaAdderOrEnderReportsCorrectRowCount() {
        testUpdateReportsCorrectRowCount { table: TableForTest ->
            UpdateBuilderImpl(table, updateSqlBuilder, argPlaceholder).set(table.int, 5).update()
        }
    }


    @Test
    fun testUpdateViaPredicateAdderOrEnderReportsCorrectRowCount() {
        testUpdateReportsCorrectRowCount { table: TableForTest ->
            UpdateBuilderImpl(table, updateSqlBuilder, argPlaceholder)
                    .set(table.int, 5)
                    .where()
                    .eq(table.int, 6)
                    .update()
        }
    }


    @Test
    fun testStatementGetsClosedEvenIfUpdateViaAdderOrEnderThrows() {
        testStatementGetsClosedEvenIfUpdateThrows { table: TableForTest ->
            UpdateBuilderImpl(table, updateSqlBuilder, argPlaceholder).set(table.int, 5).update()
        }
    }


    @Test
    fun testStatementGetsClosedEvenIfUpdateViaPredicateAdderOrEnderThrows() {
        testStatementGetsClosedEvenIfUpdateThrows { table: TableForTest ->
            UpdateBuilderImpl(table, updateSqlBuilder, argPlaceholder)
                    .set(table.int, 5)
                    .where()
                    .eq(table.int, 6)
                    .update()
        }
    }


    private fun testUpdateReportsCorrectRowCount(updateOp: (TableForTest) -> Int) {
        val numberOfRowsAffected = 8745
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                updateProvider = { tableName, sql ->
                    OneShotCompiledUpdateForTest(tableName, sql, numberOfRowsAffected)
                }
        )

        val table = TableForTest(configuration = defaultTableConfiguration(engine))
        assertThat(updateOp.invoke(table)).isEqualTo(numberOfRowsAffected)
    }


    private fun testStatementGetsClosedEvenIfUpdateThrows(updateOp: (TableForTest) -> Int) {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                updateProvider = { tableName, sql ->
                    OneShotCompiledUpdateForTest(tableName, sql, shouldThrowOnExecution = true)
                }
        )

        var exceptionWasThrown = false
        try {
            updateOp.invoke(TableForTest(configuration = defaultTableConfiguration(engine)))
        } catch (e: Exception) {
            exceptionWasThrown = true
        }

        when {
            !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
            else -> {
                // Assert that the statement was not successfully executed but closed
                val statement = engine.compiledUpdates.first()
                assertThat(statement.wasExecutionAttempted).isTrue()
                assertThat(statement.isExecuted).isFalse()
                assertThat(statement.isClosed).isTrue()
            }
        }
    }
}