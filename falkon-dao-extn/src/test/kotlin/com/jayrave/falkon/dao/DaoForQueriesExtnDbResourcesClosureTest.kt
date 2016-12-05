package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.EngineForTestingDaoExtn
import com.jayrave.falkon.dao.testLib.ExceptionForTesting
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Source
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.util.*

class DaoForQueriesExtnDbResourcesClosureTest {

    @Test
    fun testFindByIdClosesSourceAndCompiledStatementForQueryOnSuccessfulExecution() {
        testSourceAndCompiledStatementForQueryIsClosedOnSuccessfulExecution {
            it.dao.findById(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()))
        }
    }

    @Test
    fun testFindByIdClosesSourceAndCompiledStatementForQueryEvenOnSourceException() {
        testSourceAndCompiledStatementForQueryAreClosedEventOnSourceException {
            it.dao.findById(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()))
        }
    }

    @Test
    fun testFindByIdClosesCompiledStatementForQueryEvenOnCompiledStatementException() {
        testCompiledStatementForQueryIsClosedEventOnCompiledStatementException {
            it.dao.findById(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()))
        }
    }

    @Test
    fun testFindAllClosesSourceAndCompiledStatementForQueryOnSuccessfulExecution() {
        testSourceAndCompiledStatementForQueryIsClosedOnSuccessfulExecution { it.dao.findAll() }
    }

    @Test
    fun testFindAllClosesSourceAndCompiledStatementForQueryEvenOnSourceException() {
        testSourceAndCompiledStatementForQueryAreClosedEventOnSourceException { it.dao.findAll() }
    }

    @Test
    fun testFindAllClosesCompiledStatementForQueryEvenOnCompiledStatementException() {
        testCompiledStatementForQueryIsClosedEventOnCompiledStatementException { it.dao.findAll() }
    }

    private fun testSourceAndCompiledStatementForQueryIsClosedOnSuccessfulExecution(
            operation: (TableForTest) -> Any?) {

        val sourceForTest = SourceForTest.buildEmptySource()
        val engine = buildEngineForTestingDaoExtn(
                buildSuccessfullyExecutingCompiledStatementForQuery(sourceForTest)
        )

        operation.invoke(buildTableForTest(engine))

        // Verify source & compiled query closure
        assertThat(sourceForTest.isClosed).isTrue()
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        verify(engine.compiledStatementsForQuery.first()).close()
    }

    private fun testSourceAndCompiledStatementForQueryAreClosedEventOnSourceException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val sourceForTest = SourceForTest.buildSourceThatThrowsExceptionOnTryingToMove()
        val engine = buildEngineForTestingDaoExtn(
                buildSuccessfullyExecutingCompiledStatementForQuery(sourceForTest)
        )

        try {
            operation.invoke(buildTableForTest(engine))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and source & compiled query were closed
        assertThat(exceptionCaught).isTrue()
        assertThat(sourceForTest.isClosed).isTrue()
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        verify(engine.compiledStatementsForQuery.first()).close()
    }

    private fun testCompiledStatementForQueryIsClosedEventOnCompiledStatementException(
            operation: (TableForTest) -> Any?) {

        var exceptionCaught = false
        val engine = buildEngineForTestingDaoExtn(
                buildCompiledStatementForQueryThatThrowsOnExecuting()
        )

        try {
            operation.invoke(buildTableForTest(engine))
        } catch (e: ExceptionForTesting) {
            exceptionCaught = true
        }

        if (!exceptionCaught) {
            fail("Exception must have been thrown")
        }

        // Verify exception was thrown and compiled query was closed
        assertThat(exceptionCaught).isTrue()
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        verify(engine.compiledStatementsForQuery.first()).close()
    }


    private class SourceForTest private constructor(
            private val forMoveCalls: () -> Boolean) : Source {

        override var isClosed = false
            private set

        override fun close() {
            isClosed = true
        }

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

        private fun buildSuccessfullyExecutingCompiledStatementForQuery(source: Source):
                CompiledStatement<Source> {

            val CompiledStatementForQueryMock = mock<CompiledStatement<Source>>()
            whenever(CompiledStatementForQueryMock.execute()).thenAnswer { source }
            return CompiledStatementForQueryMock
        }


        private fun buildCompiledStatementForQueryThatThrowsOnExecuting():
                CompiledStatement<Source> {

            val CompiledStatementForQueryMock = mock<CompiledStatement<Source>>()
            whenever(CompiledStatementForQueryMock.execute()).thenThrow(
                    ExceptionForTesting::class.java
            )

            return CompiledStatementForQueryMock
        }


        private fun buildEngineForTestingDaoExtn(
                CompiledStatementForQuery: CompiledStatement<Source>):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn.createWithMockStatements(
                    queryProvider = { CompiledStatementForQuery }
            )
        }


        private fun buildTableForTest(engine: Engine): TableForTest {
            return TableForTest(defaultTableConfiguration(engine))
        }
    }
}