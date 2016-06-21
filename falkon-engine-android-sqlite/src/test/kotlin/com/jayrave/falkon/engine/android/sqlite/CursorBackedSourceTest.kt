package com.jayrave.falkon.engine.android.sqlite

import android.database.Cursor
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CursorBackedSourceTest {

    private val cursorMock: Cursor = mock()
    private val source = CursorBackedSource(cursorMock)

    @Test
    fun testGetPosition() {
        whenever(cursorMock.position).thenReturn(5)
        val actualPosition = source.position

        val expectedPosition = 6 // Since cursor uses 0-based index
        assertThat(actualPosition).isEqualTo(expectedPosition)
        verify(cursorMock).position
        verifyNoMoreInteractions(cursorMock)

    }

    @Test
    fun testMove() {
        whenever(cursorMock.move(eq(5))).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.move(5)
        val invalidMoveReturnValue = source.move(5)

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(cursorMock, times(2)).move(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToPosition() {
        whenever(cursorMock.moveToPosition(eq(5))).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToPosition(6)
        val invalidMoveReturnValue = source.moveToPosition(6)

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        val expectedPosition = 5 // Since cursor uses 0-based index
        verify(cursorMock, times(2)).moveToPosition(eq(expectedPosition))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToFirst() {
        whenever(cursorMock.moveToFirst()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToFirst()
        val invalidMoveReturnValue = source.moveToFirst()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(cursorMock, times(2)).moveToFirst()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToLast() {
        whenever(cursorMock.moveToLast()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToLast()
        val invalidMoveReturnValue = source.moveToLast()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(cursorMock, times(2)).moveToLast()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToNext() {
        whenever(cursorMock.moveToNext()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToNext()
        val invalidMoveReturnValue = source.moveToNext()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(cursorMock, times(2)).moveToNext()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToPrevious() {
        whenever(cursorMock.moveToPrevious()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToPrevious()
        val invalidMoveReturnValue = source.moveToPrevious()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(cursorMock, times(2)).moveToPrevious()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetColumnIndex() {
        whenever(cursorMock.getColumnIndex(eq("test"))).thenReturn(5)
        val actualColumnIndex = source.getColumnIndex("test")

        val expectedIndex = 6 // Since cursor uses 0-based index
        assertThat(actualColumnIndex).isEqualTo(expectedIndex)
        verify(cursorMock).getColumnIndex(eq("test"))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetShort() {
        whenever(cursorMock.getShort(eq(4))).thenReturn(6)
        val actualValue = source.getShort(5)

        assertThat(actualValue).isEqualTo(6)
        verify(cursorMock).getShort(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetInt() {
        whenever(cursorMock.getInt(eq(4))).thenReturn(6)
        val actualValue = source.getInt(5)

        assertThat(actualValue).isEqualTo(6)
        verify(cursorMock).getInt(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetLong() {
        whenever(cursorMock.getLong(eq(4))).thenReturn(6)
        val actualValue = source.getLong(5)

        assertThat(actualValue).isEqualTo(6)
        verify(cursorMock).getLong(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetFloat() {
        whenever(cursorMock.getFloat(eq(4))).thenReturn(6F)
        val actualValue = source.getFloat(5)

        assertThat(actualValue).isEqualTo(6F)
        verify(cursorMock).getFloat(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetDouble() {
        whenever(cursorMock.getDouble(eq(4))).thenReturn(6.0)
        val actualValue = source.getDouble(5)

        assertThat(actualValue).isEqualTo(6.0)
        verify(cursorMock).getDouble(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetString() {
        whenever(cursorMock.getString(eq(4))).thenReturn("test")
        val actualValue = source.getString(5)

        assertThat(actualValue).isEqualTo("test")
        verify(cursorMock).getString(eq(4))  // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetBlob() {
        whenever(cursorMock.getBlob(eq(4))).thenReturn(byteArrayOf(6))
        val actualValue = source.getBlob(5)

        assertThat(actualValue).isEqualTo(byteArrayOf(6))
        verify(cursorMock).getBlob(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testIsNull() {
        whenever(cursorMock.isNull(eq(4))).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.isNull(5)
        val invalidMoveReturnValue = source.isNull(5)

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(cursorMock, times(2)).isNull(eq(4)) // Since cursor uses 0-based index
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testClose() {
        source.close()
        verify(cursorMock).close()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testIsClosed() {
        whenever(cursorMock.isClosed).thenReturn(false).thenReturn(true)
        val firstIsClosedReturnValue = source.isClosed()
        val secondIsClosedReturnValue = source.isClosed()

        assertThat(firstIsClosedReturnValue).isFalse()
        assertThat(secondIsClosedReturnValue).isTrue()

        verify(cursorMock, times(2)).isClosed
        verifyNoMoreInteractions(cursorMock)
    }
}