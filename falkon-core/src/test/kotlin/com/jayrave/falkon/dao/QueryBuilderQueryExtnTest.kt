package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.query.QueryBuilderImpl
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledQueryForTest
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.testLib.TableForTest
import com.jayrave.falkon.testLib.defaultTableConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class QueryBuilderQueryExtnTest {

    @Test
    fun testStatementGetsClosedEvenIfQueryViaAdderOrEnderThrows() {
        testStatementGetsClosedEvenIfQueryThrows { table: TableForTest ->
            QueryBuilderImpl(table).distinct().query()
        }
    }


    @Test
    fun testStatementGetsClosedEvenIfQueryViaPredicateAdderOrEnderThrows() {
        testStatementGetsClosedEvenIfQueryThrows { table: TableForTest ->
            QueryBuilderImpl(table).where().eq(table.int, 5).query()
        }
    }


    private fun testStatementGetsClosedEvenIfQueryThrows(queryOp: (TableForTest) -> Source) {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                queryProvider = { OneShotCompiledQueryForTest(it, shouldThrowOnExecution = true) }
        )

        var exceptionWasThrown = false
        try {
            queryOp.invoke(TableForTest(defaultTableConfiguration(engine)))
        } catch (e: Exception) {
            exceptionWasThrown = true
        }

        when {
            !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
            else -> {
                // Assert that the statement was not successfully executed but closed
                val statement = engine.compiledQueries.first()
                assertThat(statement.wasExecutionAttempted).isTrue()
                assertThat(statement.isExecuted).isFalse()
                assertThat(statement.isClosed).isTrue()
            }
        }
    }
}