package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.delete.DeleteBuilderImpl
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledDeleteForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class DeleteBuilderDeleteExtnTest {

    @Test
    fun testDeleteViaDeleteBuilderReportsCorrectRowCount() {
        testDeleteReportsCorrectRowCount { table: TableForTest ->
            DeleteBuilderImpl(table).delete()
        }
    }


    @Test
    fun testDeleteViaAdderOrEnderReportsCorrectRowCount() {
        testDeleteReportsCorrectRowCount { table: TableForTest ->
            DeleteBuilderImpl(table).where().eq(table.int, 6).delete()
        }
    }


    @Test
    fun testStatementGetsClosedEvenIfDeleteViaDeleteBuilderThrows() {
        testStatementGetsClosedEvenIfDeleteThrows { table: TableForTest ->
            DeleteBuilderImpl(table).delete()
        }
    }


    @Test
    fun testStatementGetsClosedEvenIfDeleteViaAdderOrEnderThrows() {
        testStatementGetsClosedEvenIfDeleteThrows { table: TableForTest ->
            DeleteBuilderImpl(table).where().eq(table.int, 6).delete()
        }
    }


    private fun testDeleteReportsCorrectRowCount(deleteOp: (TableForTest) -> Int) {
        val numberOfRowsAffected = 8745
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                deleteProvider = { OneShotCompiledDeleteForTest(it, numberOfRowsAffected) }
        )

        val table = TableForTest(defaultTableConfiguration(engine))
        assertThat(deleteOp.invoke(table)).isEqualTo(numberOfRowsAffected)
    }


    private fun testStatementGetsClosedEvenIfDeleteThrows(deleteOp: (TableForTest) -> Int) {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                deleteProvider = { OneShotCompiledDeleteForTest(it, shouldThrowOnExecution = true) }
        )

        var exceptionWasThrown = false
        try {
            deleteOp.invoke(TableForTest(defaultTableConfiguration(engine)))
        } catch (e: Exception) {
            exceptionWasThrown = true
        }

        when {
            !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
            else -> {
                // Assert that the statement was not successfully executed but closed
                val statement = engine.compiledDeletes.first()
                assertThat(statement.wasExecutionAttempted).isTrue()
                assertThat(statement.isExecuted).isFalse()
                assertThat(statement.isClosed).isTrue()
            }
        }
    }
}