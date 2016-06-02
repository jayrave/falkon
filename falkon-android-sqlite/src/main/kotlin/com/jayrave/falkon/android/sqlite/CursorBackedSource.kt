package com.jayrave.falkon.android.sqlite

import android.database.Cursor
import com.jayrave.falkon.engine.Source

class CursorBackedSource(private val cursor: Cursor) : Source {

    override val position: Int
        get() = cursor.position

    override fun move(offset: Int): Boolean = cursor.move(offset)
    override fun moveToPosition(position: Int): Boolean = cursor.moveToPosition(position)
    override fun moveToFirst(): Boolean = cursor.moveToFirst()
    override fun moveToLast(): Boolean = cursor.moveToLast()
    override fun moveToNext(): Boolean = cursor.moveToNext()
    override fun moveToPrevious(): Boolean = cursor.moveToPrevious()
    override fun getColumnIndex(columnName: String): Int = cursor.getColumnIndex(columnName)
    override fun getShort(columnIndex: Int): Short = cursor.getShort(columnIndex)
    override fun getInt(columnIndex: Int): Int = cursor.getInt(columnIndex)
    override fun getLong(columnIndex: Int): Long = cursor.getLong(columnIndex)
    override fun getFloat(columnIndex: Int): Float = cursor.getFloat(columnIndex)
    override fun getDouble(columnIndex: Int): Double = cursor.getDouble(columnIndex)
    override fun getString(columnIndex: Int): String = cursor.getString(columnIndex)
    override fun getBlob(columnIndex: Int): ByteArray = cursor.getBlob(columnIndex)
    override fun isNull(columnIndex: Int): Boolean = cursor.isNull(columnIndex)
}