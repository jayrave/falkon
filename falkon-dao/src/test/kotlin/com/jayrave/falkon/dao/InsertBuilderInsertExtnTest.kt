package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.insert.InsertBuilderImpl
import com.jayrave.falkon.dao.insert.testLib.InsertSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledInsertForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class InsertBuilderInsertExtnTest {

    private val argPlaceholder = "?"
    private val insertSqlBuilder = InsertSqlBuilderForTesting()

    @Test
    fun testInsertReturnsTrueForSingleRowInsertion() {
        testInsertBuilderExecutionReturnsAppropriateFlag(1, true)
    }


    @Test
    fun testInsertReturnsFalseForNonSingleRowInsertion() {
        testInsertBuilderExecutionReturnsAppropriateFlag(-1, false)
        testInsertBuilderExecutionReturnsAppropriateFlag(0, false)
        testInsertBuilderExecutionReturnsAppropriateFlag(2, false)
    }


    @Test
    fun testStatementGetsClosedEvenIfInsertThrows() {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                insertProvider = { OneShotCompiledInsertForTest(it, shouldThrowOnExecution = true) }
        )

        val table = TableForTest(defaultTableConfiguration(engine))
        val builder = InsertBuilderImpl(table, insertSqlBuilder, argPlaceholder)

        var exceptionWasThrown = false
        try {
            builder.set(table.int, 5).insert()
        } catch (e: Exception) {
            exceptionWasThrown = true
        }

        when {
            !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
            else -> {
                // Assert that the statement was not successfully executed but closed
                val statement = engine.compiledInserts.first()
                assertThat(statement.wasExecutionAttempted).isTrue()
                assertThat(statement.isExecuted).isFalse()
                assertThat(statement.isClosed).isTrue()
            }
        }
    }


    private fun testInsertBuilderExecutionReturnsAppropriateFlag(
            numberOfRowsInserted: Int, expectedFlag: Boolean) {

        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                insertProvider = { OneShotCompiledInsertForTest(it, numberOfRowsInserted) }
        )

        val table = TableForTest(defaultTableConfiguration(engine))
        val builder = InsertBuilderImpl(table, insertSqlBuilder, argPlaceholder)
        assertThat(builder.set(table.int, 5).insert()).isEqualTo(expectedFlag)

        // Assert that the statement was executed and closed
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }
}