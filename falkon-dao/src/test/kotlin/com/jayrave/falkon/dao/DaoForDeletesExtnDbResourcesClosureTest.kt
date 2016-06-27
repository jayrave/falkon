package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.engine.CompiledDelete
import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.util.*

class DaoForDeletesExtnDbResourcesClosureTest {

    @Test
    fun testDeletionOfSingleModelClosesCompiledDeleteOnSuccessfulExecution() {
        testCompiledDeleteIsClosedOnSuccessfulExecution { it.dao.delete(ModelForTest()) }
    }

    @Test
    fun testDeletionOfSingleModelClosesCompiledDeleteEvenOnException() {
        testCompiledDeleteIsClosedEventOnException { it.dao.delete(ModelForTest()) }
    }

    @Test
    fun testDeletionOfVarargModelsClosesCompiledDeleteOnSuccessfulExecution() {
        testCompiledDeleteIsClosedOnSuccessfulExecution {
            it.dao.delete(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testDeletionOfVarargModelsClosesCompiledDeleteEvenOnException() {
        testCompiledDeleteIsClosedEventOnException {
            it.dao.delete(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testDeletionOfModelIterableClosesCompiledDeleteOnSuccessfulExecution() {
        testCompiledDeleteIsClosedOnSuccessfulExecution { it.dao.delete(listOf(ModelForTest())) }
    }

    @Test
    fun testDeletionOfModelIterableClosesCompiledDeleteEvenOnException() {
        testCompiledDeleteIsClosedEventOnException { it.dao.delete(listOf(ModelForTest())) }
    }

    @Test
    fun testDeletionByIdOfSingleModelClosesCompiledDeleteOnSuccessfulExecution() {
        testCompiledDeleteIsClosedOnSuccessfulExecution { it.dao.deleteById(UUID.randomUUID()) }
    }

    @Test
    fun testDeletionByIdOfSingleModelClosesCompiledDeleteEvenOnException() {
        testCompiledDeleteIsClosedEventOnException { it.dao.deleteById(UUID.randomUUID()) }
    }

    @Test
    fun testDeletionByIdOfVarargModelsClosesCompiledDeleteOnSuccessfulExecution() {
        testCompiledDeleteIsClosedOnSuccessfulExecution {
            it.dao.deleteById(UUID.randomUUID(), UUID.randomUUID())
        }
    }

    @Test
    fun testDeletionByIdOfVarargModelsClosesCompiledDeleteEvenOnException() {
        testCompiledDeleteIsClosedEventOnException {
            it.dao.deleteById(UUID.randomUUID(), UUID.randomUUID())
        }
    }

    @Test
    fun testDeletionByIdOfModelIterableClosesCompiledDeleteOnSuccessfulExecution() {
        testCompiledDeleteIsClosedOnSuccessfulExecution {
            it.dao.deleteById(listOf(UUID.randomUUID()))
        }
    }

    @Test
    fun testDeletionByIdOfModelIterableClosesCompiledDeleteEvenOnException() {
        testCompiledDeleteIsClosedEventOnException {
            it.dao.deleteById(listOf(UUID.randomUUID()))
        }
    }

    private fun testCompiledDeleteIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(buildSuccessfullyExecutingCompiledDelete())
        operation.invoke(buildTableForTest(engine))

        assertThat(engine.compiledDeletes).hasSize(1)
        verify(engine.compiledDeletes.first()).close()
    }

    private fun testCompiledDeleteIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(buildCompiledDeleteThatThrowsOnExecuting())

        try {
            operation.invoke(buildTableForTest(engine))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and compiled delete was closed
        assertThat(exceptionCaught).isTrue()
        assertThat(engine.compiledDeletes).hasSize(1)
        verify(engine.compiledDeletes.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledDelete(): CompiledDelete {
            return mock()
        }


        private fun buildCompiledDeleteThatThrowsOnExecuting(): CompiledDelete {
            val compiledDeleteMock = mock<CompiledDelete>()
            whenever(compiledDeleteMock.execute()).thenThrow(ExceptionForTesting::class.java)
            return compiledDeleteMock
        }


        private fun buildEngineForTestingDaoExtn(compiledDelete: CompiledDelete):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    deleteProvider = { compiledDelete }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}