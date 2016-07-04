package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.engine.CompiledUpdate
import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class DaoForUpdatesExtnDbResourcesClosureTest {

    @Test
    fun testUpdateOfSingleModelClosesCompiledUpdateOnSuccessfulExecution() {
        testCompiledUpdateIsClosedOnSuccessfulExecution { it.dao.update(ModelForTest()) }
    }

    @Test
    fun testUpdateOfSingleModelClosesCompiledUpdateEvenOnException() {
        testCompiledUpdateIsClosedEventOnException { it.dao.update(ModelForTest()) }
    }

    @Test
    fun testUpdateOfVarargModelsClosesCompiledUpdateOnSuccessfulExecution() {
        testCompiledUpdateIsClosedOnSuccessfulExecution {
            it.dao.update(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testUpdateOfVarargModelsClosesCompiledUpdateEvenOnException() {
        testCompiledUpdateIsClosedEventOnException {
            it.dao.update(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testUpdateOfModelIterableClosesCompiledUpdateOnSuccessfulExecution() {
        testCompiledUpdateIsClosedOnSuccessfulExecution { it.dao.update(listOf(ModelForTest())) }
    }

    @Test
    fun testUpdateOfModelIterableClosesCompiledUpdateEvenOnException() {
        testCompiledUpdateIsClosedEventOnException { it.dao.update(listOf(ModelForTest())) }
    }

    private fun testCompiledUpdateIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(buildSuccessfullyExecutingCompiledUpdate())
        operation.invoke(buildTableForTest(engine))

        assertThat(engine.compiledUpdates).hasSize(1)
        verify(engine.compiledUpdates.first()).close()
    }

    private fun testCompiledUpdateIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(buildCompiledUpdateThatThrowsOnExecuting())

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
        assertThat(engine.compiledUpdates).hasSize(1)
        verify(engine.compiledUpdates.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledUpdate(): CompiledUpdate {
            return mock()
        }


        private fun buildCompiledUpdateThatThrowsOnExecuting(): CompiledUpdate {
            val compiledUpdateMock = mock<CompiledUpdate>()
            whenever(compiledUpdateMock.execute()).thenThrow(ExceptionForTesting::class.java)
            return compiledUpdateMock
        }


        private fun buildEngineForTestingDaoExtn(compiledUpdate: CompiledUpdate):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    updateProvider = { compiledUpdate }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}