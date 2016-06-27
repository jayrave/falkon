package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.engine.CompiledInsert
import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class DaoForInsertsExtnDbResourcesClosureTest {

    @Test
    fun testInsertOfSingleModelClosesCompiledInsertOnSuccessfulExecution() {
        testCompiledInsertIsClosedOnSuccessfulExecution { it.dao.insert(ModelForTest()) }
    }

    @Test
    fun testInsertOfSingleModelClosesCompiledInsertEvenOnException() {
        testCompiledInsertIsClosedEventOnException { it.dao.insert(ModelForTest()) }
    }

    @Test
    fun testInsertOfVarargModelsClosesCompiledInsertOnSuccessfulExecution() {
        testCompiledInsertIsClosedOnSuccessfulExecution {
            it.dao.insert(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testInsertOfVarargModelsClosesCompiledInsertEvenOnException() {
        testCompiledInsertIsClosedEventOnException {
            it.dao.insert(ModelForTest(), ModelForTest())
        }
    }

    @Test
    fun testInsertOfModelIterableClosesCompiledInsertOnSuccessfulExecution() {
        testCompiledInsertIsClosedOnSuccessfulExecution { it.dao.insert(listOf(ModelForTest())) }
    }

    @Test
    fun testInsertOfModelIterableClosesCompiledInsertEvenOnException() {
        testCompiledInsertIsClosedEventOnException { it.dao.insert(listOf(ModelForTest())) }
    }

    private fun testCompiledInsertIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val engine = buildEngineForTestingDaoExtn(buildSuccessfullyExecutingCompiledInsert())
        operation.invoke(buildTableForTest(engine))

        assertThat(engine.compiledInserts).hasSize(1)
        verify(engine.compiledInserts.first()).close()
    }

    private fun testCompiledInsertIsClosedEventOnException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(buildCompiledInsertThatThrowsOnExecuting())

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
        assertThat(engine.compiledInserts).hasSize(1)
        verify(engine.compiledInserts.first()).close()
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledInsert(): CompiledInsert {
            return mock()
        }


        private fun buildCompiledInsertThatThrowsOnExecuting(): CompiledInsert {
            val compiledInsertMock = mock<CompiledInsert>()
            whenever(compiledInsertMock.execute()).thenThrow(ExceptionForTesting::class.java)
            return compiledInsertMock
        }


        private fun buildEngineForTestingDaoExtn(compiledInsert: CompiledInsert):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    insertProvider = { compiledInsert }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}