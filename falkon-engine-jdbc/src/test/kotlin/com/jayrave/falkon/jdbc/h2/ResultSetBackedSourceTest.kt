package com.jayrave.falkon.jdbc.h2

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Blob
import java.sql.ResultSet

/**
 * Just to make sure that the calls are forwarded to appropriate methods in [ResultSet] and
 * return values are appropriately returned
 */
class ResultSetBackedSourceTest {

    private val resultSetMock: ResultSet = mock()
    private val source = ResultSetBackedSource(resultSetMock)

    @Test
    fun testGetPosition() {
        whenever(resultSetMock.row).thenReturn(5)
        val actualPosition = source.position

        assertThat(actualPosition).isEqualTo(5)
        verify(resultSetMock).row
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testMove() {
        whenever(resultSetMock.relative(eq(5))).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.move(5)
        val invalidMoveReturnValue = source.move(5)

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).relative(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testMoveToPosition() {
        whenever(resultSetMock.absolute(eq(5))).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToPosition(5)
        val invalidMoveReturnValue = source.moveToPosition(5)

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).absolute(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testMoveToFirst() {
        whenever(resultSetMock.first()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToFirst()
        val invalidMoveReturnValue = source.moveToFirst()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).first()
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testMoveToLast() {
        whenever(resultSetMock.last()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToLast()
        val invalidMoveReturnValue = source.moveToLast()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).last()
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testMoveToNext() {
        whenever(resultSetMock.next()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToNext()
        val invalidMoveReturnValue = source.moveToNext()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).next()
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testMoveToPrevious() {
        whenever(resultSetMock.previous()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.moveToPrevious()
        val invalidMoveReturnValue = source.moveToPrevious()

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).previous()
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetColumnIndex() {
        whenever(resultSetMock.findColumn(eq("test"))).thenReturn(5)
        val actualColumnIndex = source.getColumnIndex("test")

        assertThat(actualColumnIndex).isEqualTo(5)
        verify(resultSetMock).findColumn(eq("test"))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetShort() {
        whenever(resultSetMock.getShort(eq(5))).thenReturn(6)
        val actualValue = source.getShort(5)

        assertThat(actualValue).isEqualTo(6)
        verify(resultSetMock).getShort(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetInt() {
        whenever(resultSetMock.getInt(eq(5))).thenReturn(6)
        val actualValue = source.getInt(5)

        assertThat(actualValue).isEqualTo(6)
        verify(resultSetMock).getInt(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetLong() {
        whenever(resultSetMock.getLong(eq(5))).thenReturn(6)
        val actualValue = source.getLong(5)

        assertThat(actualValue).isEqualTo(6)
        verify(resultSetMock).getLong(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetFloat() {
        whenever(resultSetMock.getFloat(eq(5))).thenReturn(6F)
        val actualValue = source.getFloat(5)

        assertThat(actualValue).isEqualTo(6F)
        verify(resultSetMock).getFloat(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetDouble() {
        whenever(resultSetMock.getDouble(eq(5))).thenReturn(6.0)
        val actualValue = source.getDouble(5)

        assertThat(actualValue).isEqualTo(6.0)
        verify(resultSetMock).getDouble(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetString() {
        whenever(resultSetMock.getString(eq(5))).thenReturn("test")
        val actualValue = source.getString(5)

        assertThat(actualValue).isEqualTo("test")
        verify(resultSetMock).getString(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testGetBlob() {
        val blobMock: Blob = mock()
        val inputByteArray = byteArrayOf(5)
        whenever(blobMock.length()).thenReturn(5)
        whenever(blobMock.getBytes(eq(1), eq(5))).thenReturn(inputByteArray)
        whenever(resultSetMock.getBlob(eq(5))).thenReturn(blobMock)
        val actualValue = source.getBlob(5)

        assertThat(actualValue).isEqualTo(inputByteArray)
        verify(resultSetMock).getBlob(eq(5))
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun testIsNull() {
        whenever(resultSetMock.wasNull()).thenReturn(true).thenReturn(false)
        val validMoveReturnValue = source.isNull(5)
        val invalidMoveReturnValue = source.isNull(5)

        assertThat(validMoveReturnValue).isTrue()
        assertThat(invalidMoveReturnValue).isFalse()

        verify(resultSetMock, times(2)).getObject(5)
        verify(resultSetMock, times(2)).wasNull()
        verifyNoMoreInteractions(resultSetMock)
    }
}