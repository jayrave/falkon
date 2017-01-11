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

class DaoForInsertsDbResourcesClosureTest {

    @Test
    fun testInsertOfSingleModelClosesCompiledStatementForInsertOnSuccessfulExecution() {
        testCompiledStatementForInsertIsClosedOnSuccessfulExecution {
            it.dao.insert(ModelForTest())
        }
    }

    @Test
    fun testInsertOfSingleModelClosesCompiledStatementForInsertEvenOnException() {
        testCompiledStatementForInsertIsClosedEventOnException { it.dao.insert(ModelForTest()) }
    }

    @Test
    fun testInsertOfVarargModelsClosesCompiledStatementForInsertOnSuccessfulExecution() {
        testCompiledStatementForInsertIsClosedOnSuccessfulExecution {
            it.dao.insert(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testInsertOfVarargModelsClosesCompiledStatementForInsertEvenOnException() {
        testCompiledStatementForInsertIsClosedEventOnException {
            it.dao.insert(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testInsertOfModelIterableClosesCompiledStatementForInsertOnSuccessfulExecution() {
        testCompiledStatementForInsertIsClosedOnSuccessfulExecution {
            it.dao.insert(listOf(ModelForTest()))
        }
    }

    @Test
    fun testInsertOfModelIterableClosesCompiledStatementForInsertEvenOnException() {
        testCompiledStatementForInsertIsClosedEventOnException {
            it.dao.insert(listOf(ModelForTest()))
        }
    }

    private fun testCompiledStatementForInsertIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(
                buildSuccessfullyExecutingCompiledStatementForInsert())
        operation.invoke(buildTableForTest(engine))

        assertThat(engine.compiledStatementsForInsert).hasSize(1)
        verify(engine.compiledStatementsForInsert.first()).close()
    }

    private fun testCompiledStatementForInsertIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(
                buildCompiledStatementForInsertThatThrowsOnExecuting())

        try {
            operation.invoke(buildTableForTest(engine))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and compiled insert was closed
        assertThat(exceptionCaught).isTrue()
        assertThat(engine.compiledStatementsForInsert).hasSize(1)
        verify(engine.compiledStatementsForInsert.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledStatementForInsert(): CompiledStatement<Int> {
            val compiledStatementForInsertMock = mock<CompiledStatement<Int>>()
            whenever(compiledStatementForInsertMock.execute()).thenReturn(0)
            return compiledStatementForInsertMock
        }


        private fun buildCompiledStatementForInsertThatThrowsOnExecuting(): CompiledStatement<Int> {
            val compiledStatementForInsertMock = mock<CompiledStatement<Int>>()
            whenever(compiledStatementForInsertMock.execute()).thenThrow(
                    ExceptionForTesting::class.java
            )

            return compiledStatementForInsertMock
        }


        private fun buildEngineForTestingDaoExtn(
                compiledStatementForInsert: CompiledStatement<Int>):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    insertProvider = { compiledStatementForInsert }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}