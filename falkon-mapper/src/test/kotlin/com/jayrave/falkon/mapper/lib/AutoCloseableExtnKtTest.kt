package com.jayrave.falkon.mapper.lib

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class AutoCloseableExtnKtTest {

    @Test
    fun testAutoCloseableClosesOnSuccessfulExecution() {
        val autoCloseableMock = mock<AutoCloseable>()
        autoCloseableMock.safeCloseAfterOp {  }
        verify(autoCloseableMock).close()
        verifyNoMoreInteractions(autoCloseableMock)
    }


    @Test
    fun testAutoCloseableClosesEvenIfExceptionIsThrown() {
        val autoCloseableMock = mock<AutoCloseable>()
        var exceptionWasThrown = false
        try {
            autoCloseableMock.safeCloseAfterOp { throw RuntimeException() }
        } catch (e: RuntimeException) {
            exceptionWasThrown = true
        }

        if (!exceptionWasThrown) {
            fail("exception must have been thrown")
        }

        verify(autoCloseableMock).close()
        verifyNoMoreInteractions(autoCloseableMock)
    }
}