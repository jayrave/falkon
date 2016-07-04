package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.util.concurrent.Callable

class CompiledStatementKtTest {

    private val compiledStatement = mock<CompiledStatement<Unit>>()

    @Test
    fun testBindWithShortValue() {
        compiledStatement.bind(1, 5.toShort())
        verify(compiledStatement).bindShort(eq(1), eq(5.toShort()))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithIntValue() {
        compiledStatement.bind(1, 5)
        verify(compiledStatement).bindInt(eq(1), eq(5))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithLongValue() {
        compiledStatement.bind(1, 5L)
        verify(compiledStatement).bindLong(eq(1), eq(5L))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithFloatValue() {
        compiledStatement.bind(1, 5F)
        verify(compiledStatement).bindFloat(eq(1), eq(5F))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithDoubleValue() {
        compiledStatement.bind(1, 5.0)
        verify(compiledStatement).bindDouble(eq(1), eq(5.0))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithStringValue() {
        compiledStatement.bind(1, "test 5")
        verify(compiledStatement).bindString(eq(1), eq("test 5"))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithBlobValue() {
        compiledStatement.bind(1, byteArrayOf(5))
        verify(compiledStatement).bindBlob(eq(1), eq(byteArrayOf(5)))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindWithNullValue() {
        compiledStatement.bind(1, TypedNull(Type.INT))
        compiledStatement.bind(2, TypedNull(Type.LONG))

        verify(compiledStatement).bindNull(eq(1), eq(Type.INT))
        verify(compiledStatement).bindNull(eq(2), eq(Type.LONG))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testNonNativeValueGetsBoundAsString() {
        val noOpRunnable = Runnable {  }
        compiledStatement.bind(1, noOpRunnable)
        verify(compiledStatement).bindString(eq(1), eq(noOpRunnable.toString()))
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testBindAllWithDefaultStartIndex() {
        val noOpCallable = Callable { }
        val values = listOf(
                12.toShort(), 13, 14L, 15F, 16.0, "test 17", byteArrayOf(18),
                TypedNull(Type.BLOB), noOpCallable
        )

        compiledStatement.bindAll(values)
        verifyInteractionsForBindAll(
                startIndex = 1, short = 12, int = 13, long = 14, float = 15F, double = 16.0,
                string = "test 17", blob = byteArrayOf(18), nullType = Type.BLOB,
                nonNativeValue = noOpCallable
        )
    }


    @Test
    fun testBindAllWithCustomStartIndex() {
        val noOpCallable = Callable { }
        val values = listOf(
                12.toShort(), 13, 14L, 15F, 16.0, "test 17", byteArrayOf(18),
                TypedNull(Type.BLOB), noOpCallable
        )

        compiledStatement.bindAll(values, 7)
        verifyInteractionsForBindAll(
                startIndex = 7, short = 12, int = 13, long = 14, float = 15F, double = 16.0,
                string = "test 17", blob = byteArrayOf(18), nullType = Type.BLOB,
                nonNativeValue = noOpCallable
        )
    }


    @Test
    fun testCloseIfOpThrowsDoesNotCloseIfNothingThrows() {
        val compiledStatement = mock<CompiledStatement<Unit>>()
        compiledStatement.closeIfOpThrows<CompiledStatement<Unit>, Unit, Unit> { Unit }
        verifyZeroInteractions(compiledStatement)
    }


    @Test
    fun testCloseIfOpThrowsClosesIfOpThrows() {
        val compiledStatement = mock<CompiledStatement<Unit>>()
        var exceptionWasThrown = false
        try {
            compiledStatement.closeIfOpThrows<CompiledStatement<Unit>, Unit, Unit> {
                throw Throwable()
            }

        } catch (t: Throwable) {
            exceptionWasThrown = true
        }

        if (!exceptionWasThrown) {
            fail("exception must have been thrown")
        }

        verify(compiledStatement).close()
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testCloseIfOpThrowsSwallowsExceptionsThrownOnClose() {
        val compiledStatement = mock<CompiledStatement<Unit>>()
        whenever(compiledStatement.close()).thenThrow(RuntimeException::class.java)

        var exceptionWasThrown = false
        try {
            compiledStatement.closeIfOpThrows<CompiledStatement<Unit>, Unit, Unit> {
                throw Throwable()
            }

        } catch (t: Throwable) {
            exceptionWasThrown = true
        }

        if (!exceptionWasThrown) {
            fail("exception must have been thrown")
        }

        verify(compiledStatement).close()
        verifyNoMoreInteractions(compiledStatement)
    }


    @Test
    fun testCompiledStatementClosesOnSuccessfulExecution() {
        val compiledStatementMock = mock<CompiledStatement<Unit>>()
        compiledStatementMock.safeCloseAfterExecution()

        verify(compiledStatementMock).execute()
        verify(compiledStatementMock).close()
        verifyNoMoreInteractions(compiledStatementMock)
    }


    @Test
    fun testCompiledStatementClosesEvenIfExceptionIsThrown() {
        val compiledStatementMock = mock<CompiledStatement<Unit>>()
        whenever(compiledStatementMock.execute()).thenThrow(RuntimeException::class.java)

        var exceptionWasThrown = false
        try {
            compiledStatementMock.safeCloseAfterExecution()
        } catch (e: RuntimeException) {
            exceptionWasThrown = true
        }

        if (!exceptionWasThrown) {
            fail("exception must have been thrown")
        }

        verify(compiledStatementMock).execute()
        verify(compiledStatementMock).close()
        verifyNoMoreInteractions(compiledStatementMock)
    }


    private fun verifyInteractionsForBindAll(
            startIndex: Int, short: Short, int: Int, long: Long, float: Float, double: Double,
            string: String, blob: ByteArray, nullType: Type, nonNativeValue: Any) {

        verify(compiledStatement).bindShort(eq(startIndex), eq(short))
        verify(compiledStatement).bindInt(eq(startIndex + 1), eq(int))
        verify(compiledStatement).bindLong(eq(startIndex + 2), eq(long))
        verify(compiledStatement).bindFloat(eq(startIndex + 3), eq(float))
        verify(compiledStatement).bindDouble(eq(startIndex + 4), eq(double))
        verify(compiledStatement).bindString(eq(startIndex + 5), eq(string))
        verify(compiledStatement).bindBlob(eq(startIndex + 6), eq(blob))
        verify(compiledStatement).bindNull(eq(startIndex + 7), eq(nullType))
        verify(compiledStatement).bindString(eq(startIndex + 8), eq(nonNativeValue.toString()))
        verifyNoMoreInteractions(compiledStatement)
    }
}