package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class DaoForInsertOrReplacesDbResourcesClosureTest {

    @Test
    fun testInsertOrReplaceOfSingleModelClosesCompiledStatementOnSuccessfulExecution() {
        testCompiledStatementIsClosedOnSuccessfulExecution {
            it.dao.insertOrReplace(ModelForTest())
        }
    }

    @Test
    fun testInsertOrReplaceOfSingleModelClosesCompiledStatementEvenOnException() {
        testCompiledStatementIsClosedEventOnException {
            it.dao.insertOrReplace(ModelForTest())
        }
    }

    @Test
    fun testInsertOrReplaceOfVarargModelsClosesCompiledStatementOnSuccessfulExecution() {
        testCompiledStatementIsClosedOnSuccessfulExecution {
            it.dao.insertOrReplace(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testInsertOrReplaceOfVarargModelsClosesCompiledStatementEvenOnException() {
        testCompiledStatementIsClosedEventOnException {
            it.dao.insertOrReplace(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testInsertOrReplaceOfModelIterableClosesCompiledStatementOnSuccessfulExecution() {
        testCompiledStatementIsClosedOnSuccessfulExecution {
            it.dao.insertOrReplace(listOf(ModelForTest()))
        }
    }

    @Test
    fun testInsertOrReplaceOfModelIterableClosesCompiledStatementEvenOnException() {
        testCompiledStatementIsClosedEventOnException {
            it.dao.insertOrReplace(listOf(ModelForTest()))
        }
    }

    private fun testCompiledStatementIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(
                buildSuccessfullyExecutingCompiledStatement()
        )

        operation.invoke(buildTableForTest(engine))
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        verify(engine.compiledStatementsForInsertOrReplace.first()).close()
    }

    private fun testCompiledStatementIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(
                buildCompiledStatementOrReplaceThatThrowsOnExecution()
        )

        try {
            operation.invoke(buildTableForTest(engine))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and compiled statement was closed
        assertThat(exceptionCaught).isTrue()
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        verify(engine.compiledStatementsForInsertOrReplace.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledStatement(): CompiledStatement<Int> {
            val compiledStatementMock = mock<CompiledStatement<Int>>()
            whenever(compiledStatementMock.execute()).thenReturn(0)
            return compiledStatementMock
        }


        private fun buildCompiledStatementOrReplaceThatThrowsOnExecution(): CompiledStatement<Int> {
            val compiledStatementMock = mock<CompiledStatement<Int>>()
            whenever(compiledStatementMock.execute()).thenThrow(
                    ExceptionForTesting::class.java
            )

            return compiledStatementMock
        }


        private fun buildEngineForTestingDaoExtn(compiledStatement: CompiledStatement<Int>):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    insertOrReplaceProvider = { compiledStatement }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}