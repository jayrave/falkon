package com.jayrave.falkon

import com.jayrave.falkon.engine.Sink
import com.jayrave.falkon.exceptions.DataConsumptionException

/**
 * A [DataConsumer] that forwards all the calls to a [Sink]. Failing to call [setColumnName] before every
 * #put*() call will result in [DataConsumptionException]
 */
class SinkBackedDataConsumer<S : Sink>(val sink: S) : NullHandlingDataConsumer() {

    private var columnName: String? = null

    override fun put(short: Short) {
        sink.put(getAndNullifyColumnName(), short)
    }

    override fun put(int: Int) {
        sink.put(getAndNullifyColumnName(), int)
    }

    override fun put(long: Long) {
        sink.put(getAndNullifyColumnName(), long)
    }

    override fun put(float: Float) {
        sink.put(getAndNullifyColumnName(), float)
    }

    override fun put(double: Double) {
        sink.put(getAndNullifyColumnName(), double)
    }

    override fun putNonNullString(string: String) {
        sink.put(getAndNullifyColumnName(), string)
    }

    override fun putNonNullBlob(blob: ByteArray) {
        sink.put(getAndNullifyColumnName(), blob)
    }

    override fun putNull() {
        sink.putNull(getAndNullifyColumnName())
    }

    fun setColumnName(columnName: String) {
        this.columnName = columnName
    }

    private fun getAndNullifyColumnName(): String {
        val result = when (columnName) {
            null -> throw DataConsumptionException("Calling #put without setting a column name")
            else -> columnName!!
        }

        columnName = null
        return result
    }
}