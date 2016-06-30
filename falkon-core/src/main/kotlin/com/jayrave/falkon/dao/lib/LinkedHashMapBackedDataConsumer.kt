package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.NullHandlingDataConsumer
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.exceptions.DataConsumerException
import java.util.*

/**
 * A [DataConsumer] that stores the data in a backing [LinkedHashMap].
 * Failing to call [setColumnName] before every #put*() call will result 
 * in [DataConsumerException]
 */
internal class LinkedHashMapBackedDataConsumer : NullHandlingDataConsumer() {
    
    val map = LinkedHashMap<String, Any>()

    private var columnName: String? = null

    override fun put(short: Short) {
        map.put(getAndNullifyColumnName(), short)
    }

    override fun put(int: Int) {
        map.put(getAndNullifyColumnName(), int)
    }

    override fun put(long: Long) {
        map.put(getAndNullifyColumnName(), long)
    }

    override fun put(float: Float) {
        map.put(getAndNullifyColumnName(), float)
    }

    override fun put(double: Double) {
        map.put(getAndNullifyColumnName(), double)
    }

    override fun putNonNullString(string: String) {
        map.put(getAndNullifyColumnName(), string)
    }

    override fun putNonNullBlob(blob: ByteArray) {
        map.put(getAndNullifyColumnName(), blob)
    }

    override fun putNull(type: Type) {
        map.put(getAndNullifyColumnName(), TypedNull(type))
    }

    fun setColumnName(columnName: String) {
        this.columnName = columnName
    }

    private fun getAndNullifyColumnName(): String {
        val result = when (columnName) {
            null -> throw DataConsumerException("Calling #put without setting a column name")
            else -> columnName!!
        }

        columnName = null
        return result
    }
}