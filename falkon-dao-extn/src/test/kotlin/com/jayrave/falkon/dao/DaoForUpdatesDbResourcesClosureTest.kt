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

class DaoForUpdatesDbResourcesClosureTest {

    @Test
    fun testUpdateOfSingleModelClosesCompiledStatementForUpdateOnSuccessfulExecution() {
        testCompiledStatementForUpdateIsClosedOnSuccessfulExecution {
            it.dao.update(ModelForTest())
        }
    }

    @Test
    fun testUpdateOfSingleModelClosesCompiledStatementForUpdateEvenOnException() {
        testCompiledStatementForUpdateIsClosedEventOnException { it.dao.update(ModelForTest()) }
    }

    @Test
    fun testUpdateOfVarargModelsClosesCompiledStatementForUpdateOnSuccessfulExecution() {
        testCompiledStatementForUpdateIsClosedOnSuccessfulExecution {
            it.dao.update(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testUpdateOfVarargModelsClosesCompiledStatementForUpdateEvenOnException() {
        testCompiledStatementForUpdateIsClosedEventOnException {
            it.dao.update(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testUpdateOfModelIterableClosesCompiledStatementForUpdateOnSuccessfulExecution() {
        testCompiledStatementForUpdateIsClosedOnSuccessfulExecution {
            it.dao.update(listOf(ModelForTest()))
        }
    }

    @Test
    fun testUpdateOfModelIterableClosesCompiledStatementForUpdateEvenOnException() {
        testCompiledStatementForUpdateIsClosedEventOnException {
            it.dao.update(listOf(ModelForTest()))
        }
    }

    private fun testCompiledStatementForUpdateIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(
                buildSuccessfullyExecutingCompiledStatementForUpdate()
        )

        operation.invoke(buildTableForTest(engine))

        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        verify(engine.compiledStatementsForUpdate.first()).close()
    }

    private fun testCompiledStatementForUpdateIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(
                buildCompiledStatementForUpdateThatThrowsOnExecuting()
        )

        try {
            operation.invoke(buildTableForTest(engine))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and compiled update was closed
        assertThat(exceptionCaught).isTrue()
        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        verify(engine.compiledStatementsForUpdate.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledStatementForUpdate(): CompiledStatement<Int> {
            val CompiledStatementForUpdateMock = mock<CompiledStatement<Int>>()
            whenever(CompiledStatementForUpdateMock.execute()).thenReturn(0)
            return CompiledStatementForUpdateMock
        }


        private fun buildCompiledStatementForUpdateThatThrowsOnExecuting(): CompiledStatement<Int> {
            val CompiledStatementForUpdateMock = mock<CompiledStatement<Int>>()
            whenever(CompiledStatementForUpdateMock.execute()).thenThrow(
                    ExceptionForTesting::class.java
            )

            return CompiledStatementForUpdateMock
        }


        private fun buildEngineForTestingDaoExtn(
                CompiledStatementForUpdate: CompiledStatement<Int>):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    updateProvider = { CompiledStatementForUpdate }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}