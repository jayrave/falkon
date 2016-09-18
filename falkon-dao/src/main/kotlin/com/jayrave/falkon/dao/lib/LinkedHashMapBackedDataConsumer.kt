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
    
    val map = LinkedHashMap<String, Any>()

    private var columnName: String? = null

    override fun put(short: Short?) {
        when (short) {
            null -> putNull(Type.SHORT)
            else -> map.put(getAndNullifyColumnName(), short)
        }
    }

    override fun put(int: Int?) {
        when (int) {
            null -> putNull(Type.INT)
            else -> map.put(getAndNullifyColumnName(), int)
        }
    }

    override fun put(long: Long?) {
        when (long) {
            null -> putNull(Type.LONG)
            else -> map.put(getAndNullifyColumnName(), long)
        }
    }

    override fun put(float: Float?) {
        when (float) {
            null -> putNull(Type.FLOAT)
            else -> map.put(getAndNullifyColumnName(), float)
        }
    }

    override fun put(double: Double?) {
        when (double) {
            null -> putNull(Type.DOUBLE)
            else -> map.put(getAndNullifyColumnName(), double)
        }
    }

    override fun put(string: String?) {
        when (string) {
            null -> putNull(Type.STRING)
            else -> map.put(getAndNullifyColumnName(), string)
        }
    }

    override fun put(blob: ByteArray?) {
        when (blob) {
            null -> putNull(Type.BLOB)
            else -> map.put(getAndNullifyColumnName(), blob)
        }
    }

    fun setColumnName(columnName: String) {
        this.columnName = columnName
    }

    private fun putNull(type: Type) {
        map.put(getAndNullifyColumnName(), TypedNull(type))
    }

    private fun getAndNullifyColumnName(): String {
        val result = when (columnName) {
            null -> throw RuntimeException("Calling #put without setting a column name")
            else -> columnName!!
        }

        columnName = null
        return result
    }
}