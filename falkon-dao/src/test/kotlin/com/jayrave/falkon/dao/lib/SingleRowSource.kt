package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.engine.Source
import java.util.*

/**
 * A [Source] that can hold only one row at a time and always points at the row
 */
class SingleRowSource(map: Map<String, Any?>) : Source {

    override val position: Int = 0
    override val rowCount: Int = 1
    private val values: List<Any?>
    private val columnNameToIndexMap: Map<String, Int>
    init  {
        values = ArrayList(map.size)
        columnNameToIndexMap = HashMap()
        map.forEach { entry ->
            val index = values.size
            columnNameToIndexMap[entry.key] = index
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
            0 -> true
            else -> false
        }
    }

    override fun moveToFirst(): Boolean {
        return true
    }

    override fun moveToLast(): Boolean {
        return true
    }

    override fun moveToNext(): Boolean {
        return false
    }

    override fun moveToPrevious(): Boolean {
        return false
    }

    override fun getColumnIndex(columnName: String): Int {
        return columnNameToIndexMap[columnName]!!
    }

    override fun getShort(columnIndex: Int): Short {
        return values[columnIndex] as Short
    }

    override fun getInt(columnIndex: Int): Int {
        return values[columnIndex] as Int
    }

    override fun getLong(columnIndex: Int): Long {
        return values[columnIndex] as Long
    }

    override fun getFloat(columnIndex: Int): Float {
        return values[columnIndex] as Float
    }

    override fun getDouble(columnIndex: Int): Double {
        return values[columnIndex] as Double
    }

    override fun getString(columnIndex: Int): String {
        return values[columnIndex] as String
    }

    override fun getBlob(columnIndex: Int): ByteArray {
        return values[columnIndex] as ByteArray
    }

    override fun isNull(columnIndex: Int): Boolean {
        return values[columnIndex] == null
    }
}