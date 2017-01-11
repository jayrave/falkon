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
import java.util.*

class DaoForDeletesDbResourcesClosureTest {

    @Test
    fun testDeletionOfSingleModelClosesCompiledStatementForDeleteOnSuccessfulExecution() {
        testCompiledStatementForDeleteIsClosedOnSuccessfulExecution {
            it.dao.delete(ModelForTest())
        }
    }

    @Test
    fun testDeletionOfSingleModelClosesCompiledStatementForDeleteEvenOnException() {
        testCompiledStatementForDeleteIsClosedEventOnException { it.dao.delete(ModelForTest()) }
    }

    @Test
    fun testDeletionOfVarargModelsClosesCompiledStatementForDeleteOnSuccessfulExecution() {
        testCompiledStatementForDeleteIsClosedOnSuccessfulExecution {
            it.dao.delete(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testDeletionOfVarargModelsClosesCompiledStatementForDeleteEvenOnException() {
        testCompiledStatementForDeleteIsClosedEventOnException {
            it.dao.delete(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testDeletionOfModelIterableClosesCompiledStatementForDeleteOnSuccessfulExecution() {
        testCompiledStatementForDeleteIsClosedOnSuccessfulExecution {
            it.dao.delete(listOf(ModelForTest()))
        }
    }

    @Test
    fun testDeletionOfModelIterableClosesCompiledStatementForDeleteEvenOnException() {
        testCompiledStatementForDeleteIsClosedEventOnException {
            it.dao.delete(listOf(ModelForTest()))
        }
    }

    @Test
    fun testDeletionByIdOfSingleModelClosesCompiledStatementForDeleteOnSuccessfulExecution() {
        testCompiledStatementForDeleteIsClosedOnSuccessfulExecution {
            it.dao.deleteById(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()))
        }
    }

    @Test
    fun testDeletionByIdOfSingleModelClosesCompiledStatementForDeleteEvenOnException() {
        testCompiledStatementForDeleteIsClosedEventOnException {
            it.dao.deleteById(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()))
        }
    }

    @Test
    fun testDeletionByIdOfVarargModelsClosesCompiledStatementForDeleteOnSuccessfulExecution() {
        testCompiledStatementForDeleteIsClosedOnSuccessfulExecution {
            it.dao.deleteById(
                    TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()),
                    TableForTest.Id(UUID.randomUUID(), UUID.randomUUID())
            )
        }
    }

    @Test
    fun testDeletionByIdOfVarargModelsClosesCompiledStatementForDeleteEvenOnException() {
        testCompiledStatementForDeleteIsClosedEventOnException {
            it.dao.deleteById(
                    TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()),
                    TableForTest.Id(UUID.randomUUID(), UUID.randomUUID())
            )
        }
    }

    @Test
    fun testDeletionByIdOfModelIterableClosesCompiledStatementForDeleteOnSuccessfulExecution() {
        testCompiledStatementForDeleteIsClosedOnSuccessfulExecution {
            it.dao.deleteById(listOf(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID())))
        }
    }

    @Test
    fun testDeletionByIdOfModelIterableClosesCompiledStatementForDeleteEvenOnException() {
        testCompiledStatementForDeleteIsClosedEventOnException {
            it.dao.deleteById(listOf(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID())))
        }
    }

    private fun testCompiledStatementForDeleteIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(
                buildSuccessfullyExecutingCompiledStatementForDelete()
        )

        operation.invoke(buildTableForTest(engine))

        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        verify(engine.compiledStatementsForDelete.first()).close()
    }

    private fun testCompiledStatementForDeleteIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(
                buildCompiledStatementForDeleteThatThrowsOnExecuting()
        )

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
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        verify(engine.compiledStatementsForDelete.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledStatementForDelete(): CompiledStatement<Int> {
            val CompiledStatementForDeleteMock = mock<CompiledStatement<Int>>()
            whenever(CompiledStatementForDeleteMock.execute()).thenReturn(0)
            return CompiledStatementForDeleteMock
        }


        private fun buildCompiledStatementForDeleteThatThrowsOnExecuting(): CompiledStatement<Int> {
            val CompiledStatementForDeleteMock = mock<CompiledStatement<Int>>()
            whenever(CompiledStatementForDeleteMock.execute()).thenThrow(
                    ExceptionForTesting::class.java
            )

            return CompiledStatementForDeleteMock
        }


        private fun buildEngineForTestingDaoExtn(
                CompiledStatementForDelete: CompiledStatement<Int>):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    deleteProvider = { CompiledStatementForDelete }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}