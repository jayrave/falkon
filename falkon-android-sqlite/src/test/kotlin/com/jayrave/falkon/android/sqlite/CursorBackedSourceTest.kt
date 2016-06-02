package com.jayrave.falkon.android.sqlite

import android.database.Cursor
import com.nhaarman.mockito_kotlin.*
import org.junit.Test

/**
 * Just to make sure that the calls are forwarded to appropriate methods in [Cursor]
 */
class CursorBackedSourceTest {

    private val cursorMock: Cursor = mock()
    private val source = CursorBackedSource(cursorMock)

    @Test
    fun testGetRowCount() {
        source.rowCount
        verify(cursorMock).count
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetPosition() {
        source.position
        verify(cursorMock).position
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMove() {
        source.move(5)
        verify(cursorMock).move(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToPosition() {
        source.moveToPosition(5)
        verify(cursorMock).moveToPosition(5)
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToFirst() {
        source.moveToFirst()
        verify(cursorMock).moveToFirst()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToLast() {
        source.moveToLast()
        verify(cursorMock).moveToLast()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToNext() {
        source.moveToNext()
        verify(cursorMock).moveToNext()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testMoveToPrevious() {
        source.moveToPrevious()
        verify(cursorMock).moveToPrevious()
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetColumnIndex() {
        source.getColumnIndex("test")
        verify(cursorMock).getColumnIndex(eq("test"))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetShort() {
        source.getShort(5)
        verify(cursorMock).getShort(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetInt() {
        source.getInt(5)
        verify(cursorMock).getInt(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetLong() {
        source.getLong(5)
        verify(cursorMock).getLong(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetFloat() {
        source.getFloat(5)
        verify(cursorMock).getFloat(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetDouble() {
        source.getDouble(5)
        verify(cursorMock).getDouble(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetString() {
        whenever(cursorMock.getString(any())).thenReturn("test")
        source.getString(5)
        verify(cursorMock).getString(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testGetBlob() {
        whenever(cursorMock.getBlob(any())).thenReturn(byteArrayOf(5))
        source.getBlob(5)
        verify(cursorMock).getBlob(eq(5))
        verifyNoMoreInteractions(cursorMock)
    }

    @Test
    fun testIsNull() {
        source.isNull(5)
        verify(cursorMock).isNull(5)
        verifyNoMoreInteractions(cursorMock)
    }
}