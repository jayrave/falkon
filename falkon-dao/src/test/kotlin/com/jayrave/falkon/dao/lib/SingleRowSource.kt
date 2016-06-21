package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.engine.Source
import java.util.*

/**
 * A [Source] that can hold only one row at a time and always points at the row
 */
class SingleRowSource(map: Map<String, Any?>) : Source {

    override val position: Int = 1 // position is 1-based

    private var isClosed: Boolean = false
    private val values: List<Any?>
    private val columnNameToIndexMap: Map<String, Int>
    init  {
        values = ArrayList(map.size)
        columnNameToIndexMap = HashMap()
        map.forEach { entry ->
            val index = values.size
            columnNameToIndexMap[entry.key] = index + 1 // columnIndex is 1-based
            values.add(index, entry.value)
        }
    }

    override fun move(offset: Int): Boolean {
        return when (position) {
            0 -> true
            else -> false
        }
    }

    override fun moveToPosition(position: Int): Boolean {
        return when (position) {
            1 -> true
            else -> false
        }
    }

    override fun moveToFirst() = true
    override fun moveToLast() = true
    override fun moveToNext() = false
    override fun moveToPrevious() = false
    override fun getColumnIndex(columnName: String) = columnNameToIndexMap[columnName]!!
    override fun getShort(columnIndex: Int) = getValue(columnIndex) as Short
    override fun getInt(columnIndex: Int) = getValue(columnIndex) as Int
    override fun getLong(columnIndex: Int) = getValue(columnIndex) as Long
    override fun getFloat(columnIndex: Int) = getValue(columnIndex) as Float
    override fun getDouble(columnIndex: Int) = getValue(columnIndex) as Double
    override fun getString(columnIndex: Int) = getValue(columnIndex) as String
    override fun getBlob(columnIndex: Int) = getValue(columnIndex) as ByteArray
    override fun isNull(columnIndex: Int) = getValue(columnIndex) == null

    override fun isClosed() = isClosed
    override fun close() {
        isClosed = true
    }

    private fun getValue(columnIndex: Int): Any? {
        return values[columnIndex - 1] // columnIndex is 1-based
    }
}