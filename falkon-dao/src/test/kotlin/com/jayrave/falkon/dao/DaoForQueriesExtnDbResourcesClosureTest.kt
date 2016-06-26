package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.ExceptionForTesting
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.CompiledQuery
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Source
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.util.*

class DaoForQueriesExtnDbResourcesClosureTest {

    @Test
    fun testFindByIdClosesSourceAndCompiledQueryOnSuccessfulExecution() {
        testSourceAndCompiledQueryIsClosedOnSuccessfulExecution {
            it.dao.findById(UUID.randomUUID())
        }
    }

    @Test
    fun testFindByIdClosesSourceAndCompiledQueryEvenOnSourceException() {
        testSourceAndCompiledQueryAreClosedEventOnSourceException {
            it.dao.findById(UUID.randomUUID())
        }
    }

    @Test
    fun testFindByIdClosesCompiledQueryEvenOnCompiledQueryException() {
        testCompiledQueryIsClosedEventOnCompiledQueryException {
            it.dao.findById(UUID.randomUUID())
        }
    }

    @Test
    fun testFindAllClosesSourceAndCompiledQueryOnSuccessfulExecution() {
        testSourceAndCompiledQueryIsClosedOnSuccessfulExecution { it.dao.findAll() }
    }

    @Test
    fun testFindAllClosesSourceAndCompiledQueryEvenOnSourceException() {
        testSourceAndCompiledQueryAreClosedEventOnSourceException { it.dao.findAll() }
    }

    @Test
    fun testFindAllClosesCompiledQueryEvenOnCompiledQueryException() {
        testCompiledQueryIsClosedEventOnCompiledQueryException { it.dao.findAll() }
    }

    private fun testSourceAndCompiledQueryIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val sourceForTest = SourceForTest.buildEmptySource()
        val compiledQueryMock = buildSuccessfullyExecutingCompiledQuery(sourceForTest)
        operation.invoke(buildTableForTestWithMockEngine(compiledQueryMock))

        // Verify source & compiled query closure
        assertThat(sourceForTest.isClosed()).isTrue()
        verify(compiledQueryMock).close()
    }

    private fun testSourceAndCompiledQueryAreClosedEventOnSourceException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val sourceForTest = SourceForTest.buildSourceThatThrowsExceptionOnTryingToMove()
        val compiledQueryMock = buildSuccessfullyExecutingCompiledQuery(sourceForTest)

        try {
            operation.invoke(buildTableForTestWithMockEngine(compiledQueryMock))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and source & compiled query were closed
        assertThat(exceptionCaught).isTrue()
        assertThat(sourceForTest.isClosed()).isTrue()
        verify(compiledQueryMock).close()
    }

    private fun testCompiledQueryIsClosedEventOnCompiledQueryException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val compiledQueryMock = buildCompiledQueryThatThrowsOnExecuting()

        try {
            operation.invoke(buildTableForTestWithMockEngine(compiledQueryMock))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and compiled query was closed
        assertThat(exceptionCaught).isTrue()
        verify(compiledQueryMock).close()
    }


    private class SourceForTest private constructor(
            private val forMoveCalls: () -> Boolean) : Source {

        private var isClosed = false
        override fun isClosed() = isClosed
        override fun close() {
            isClosed = true
        }

        override val position: Int = 0
        override fun move(offset: Int) = forMoveCalls.invoke()
        override fun moveToPosition(position: Int) = forMoveCalls.invoke()
        override fun moveToFirst() = forMoveCalls.invoke()
        override fun moveToLast() = forMoveCalls.invoke()
        override fun moveToNext() = forMoveCalls.invoke()
        override fun moveToPrevious() = forMoveCalls.invoke()

        override fun getColumnIndex(columnName: String): Int = throwException()
        override fun getShort(columnIndex: Int): Short = throwException()
        override fun getInt(columnIndex: Int): Int = throwException()
        override fun getLong(columnIndex: Int): Long = throwException()
        override fun getFloat(columnIndex: Int): Float = throwException()
        override fun getDouble(columnIndex: Int): Double = throwException()
        override fun getString(columnIndex: Int): String = throwException()
        override fun getBlob(columnIndex: Int): ByteArray = throwException()
        override fun isNull(columnIndex: Int): Boolean = throwException()

        private fun <R> throwException(): R {
            throw UnsupportedOperationException()
        }


        companion object {
            fun buildEmptySource() = SourceForTest { false }
            fun buildSourceThatThrowsExceptionOnTryingToMove() = SourceForTest {
                throw ExceptionForTesting()
            }
        }
    }


    companion object {
        private fun buildSuccessfullyExecutingCompiledQuery(source: Source): CompiledQuery {
            val compiledQueryMock = mock<CompiledQuery>()
            whenever(compiledQueryMock.execute()).thenAnswer { source }
            return compiledQueryMock
        }


        private fun buildCompiledQueryThatThrowsOnExecuting(): CompiledQuery {
            val compiledQueryMock = mock<CompiledQuery>()
            whenever(compiledQueryMock.execute()).thenThrow(ExceptionForTesting::class.java)
            return compiledQueryMock
        }


        private fun buildTableForTestWithMockEngine(compiledQuery: CompiledQuery): TableForTest {
            // Mock engine to return the required CompiledQuery
            val engineMock = mock<Engine>()
            whenever(engineMock.compileQuery(any())).thenReturn(compiledQuery)

            val configuration = defaultTableConfiguration(engineMock)
            return TableForTest(configuration)
        }
    }
}