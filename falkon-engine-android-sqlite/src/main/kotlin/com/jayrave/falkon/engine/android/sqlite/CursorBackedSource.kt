package com.jayrave.falkon.engine.android.sqlite

import android.database.Cursor
import com.jayrave.falkon.engine.Source

/**
 * [Source] indices are 1-based but [Cursor] indices are 0-based. Appropriate conversions
 * will be done
 */
internal class CursorBackedSource(private val cursor: Cursor) : Source {

    override val canBacktrack: Boolean get() = true
    override val isClosed: Boolean
        get() = cursor.isClosed

    override fun moveToNext(): Boolean {
        return cursor.moveToNext()
    }

    override fun moveToPrevious(): Boolean {
        return cursor.moveToPrevious()
    }

    override fun getColumnIndex(columnName: String): Int {
        return indexFromCursorConversion(cursor.getColumnIndexOrThrow(columnName))
    }

    override fun getShort(columnIndex: Int): Short {
        return cursor.getShort(indexToCursorConversion(columnIndex))
    }

    override fun getInt(columnIndex: Int): Int {
        return cursor.getInt(indexToCursorConversion(columnIndex))
    }

    override fun getLong(columnIndex: Int): Long {
        return cursor.getLong(indexToCursorConversion(columnIndex))
    }

    override fun getFloat(columnIndex: Int): Float {
        return cursor.getFloat(indexToCursorConversion(columnIndex))
    }

    override fun getDouble(columnIndex: Int): Double {
        return cursor.getDouble(indexToCursorConversion(columnIndex))
    }

    override fun getString(columnIndex: Int): String {
        return cursor.getString(indexToCursorConversion(columnIndex))
    }

    override fun getBlob(columnIndex: Int): ByteArray {
        return cursor.getBlob(indexToCursorConversion(columnIndex))
    }

    override fun isNull(columnIndex: Int): Boolean {
        return cursor.isNull(indexToCursorConversion(columnIndex))
    }

    override fun close() {
        cursor.close()
    }

    private fun indexFromCursorConversion(index: Int): Int {
        return index + 1
    }

    private fun indexToCursorConversion(index: Int): Int {
        return index - 1
    }
}