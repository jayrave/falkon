package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.mapper.DataConsumer
import java.util.*

/**
 * A [DataConsumer] that stores the data in a backing [LinkedHashMap]. Failing to call
 * [setColumnName] before every #put*() call will result in [RuntimeException]
 */
internal class LinkedHashMapBackedDataConsumer : DataConsumer {

    /**
     * When accessed from outside, this map should be treated as a read-only map
     */
    val map = LinkedHashMap<String, Any>()
    private var columnName: String? = null

    fun setColumnName(columnName: String) {
        this.columnName = columnName
    }

    override fun put(short: Short?) {
        when (short) {
            null -> putNullTypeInMap(Type.SHORT)
            else -> putInMap(short)
        }
    }

    override fun put(int: Int?) {
        when (int) {
            null -> putNullTypeInMap(Type.INT)
            else -> putInMap(int)
        }
    }

    override fun put(long: Long?) {
        when (long) {
            null -> putNullTypeInMap(Type.LONG)
            else -> putInMap(long)
        }
    }

    override fun put(float: Float?) {
        when (float) {
            null -> putNullTypeInMap(Type.FLOAT)
            else -> putInMap(float)
        }
    }

    override fun put(double: Double?) {
        when (double) {
            null -> putNullTypeInMap(Type.DOUBLE)
            else -> putInMap(double)
        }
    }

    override fun put(string: String?) {
        when (string) {
            null -> putNullTypeInMap(Type.STRING)
            else -> putInMap(string)
        }
    }

    override fun put(blob: ByteArray?) {
        when (blob) {
            null -> putNullTypeInMap(Type.BLOB)
            else -> putInMap(blob)
        }
    }

    private fun putNullTypeInMap(type: Type) {
        putInMap(TypedNull(type))
    }

    private fun putInMap(value: Any) {
        val key = when (columnName) {
            null -> throw RuntimeException("Calling #put without setting a column name")
            else -> columnName!!
        }

        columnName = null
        map.put(key, value)
    }
}