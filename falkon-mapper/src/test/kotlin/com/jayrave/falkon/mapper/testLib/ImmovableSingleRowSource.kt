package com.jayrave.falkon.mapper.testLib

import com.jayrave.falkon.engine.Source
import java.util.*

/**
 * A [Source] that can hold only one row at a time and always points at the row & throws on
 * attempts to move around
 */
internal class ImmovableSingleRowSource(map: Map<String, Any?>) : Source {

    override val canBacktrack: Boolean = false
    override var isClosed: Boolean = false
        private set

    // `Source` contract demands user facing index map to be 1-based
    private val columnNameToUserFacingIndexMap: Map<String, Int>
    private val values: List<Any?>
    init  {
        values = ArrayList(map.size)
        columnNameToUserFacingIndexMap = HashMap()
        map.forEach { entry ->
            val index = values.size
            values.add(index, entry.value)
            columnNameToUserFacingIndexMap[entry.key] = index + 1 // columnIndex is 1-based
        }
    }

    override fun moveToNext() = false
    override fun moveToPrevious() = false
    override fun getColumnIndex(columnName: String) = columnNameToUserFacingIndexMap[columnName]!!
    override fun getShort(columnIndex: Int) = getValue(columnIndex) as Short
    override fun getInt(columnIndex: Int) = getValue(columnIndex) as Int
    override fun getLong(columnIndex: Int) = getValue(columnIndex) as Long
    override fun getFloat(columnIndex: Int) = getValue(columnIndex) as Float
    override fun getDouble(columnIndex: Int) = getValue(columnIndex) as Double
    override fun getString(columnIndex: Int) = getValue(columnIndex) as String
    override fun getBlob(columnIndex: Int) = getValue(columnIndex) as ByteArray
    override fun isNull(columnIndex: Int) = getValue(columnIndex) == null
    override fun close() { isClosed = true }

    private fun getValue(columnIndex: Int): Any? {
        return values[columnIndex - 1] // user facing index is 1-based
    }
}