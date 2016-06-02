package com.jayrave.falkon.dao

import com.jayrave.falkon.DataProducer
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.exceptions.DataProducerException

/**
 * A [DataProducer] that gets the required value from the underlying [Source]. Failing to call
 * [setColumnIndex] before every #get/is*() call will result in [DataProducerException]
 */
class SourceBackedDataProducer(val source: Source) : DataProducer {

    private var columnIndex: Int = INVALID_COLUMN_INDEX

    override fun getBlob(): ByteArray?  = source.getBlob(getAndInvalidateColumnIndex())
    override fun getDouble(): Double?  = source.getDouble(getAndInvalidateColumnIndex())
    override fun getFloat(): Float?  = source.getFloat(getAndInvalidateColumnIndex())
    override fun getInt(): Int?  = source.getInt(getAndInvalidateColumnIndex())
    override fun getLong(): Long?  = source.getLong(getAndInvalidateColumnIndex())
    override fun getShort(): Short?  = source.getShort(getAndInvalidateColumnIndex())
    override fun getString(): String?  = source.getString(getAndInvalidateColumnIndex())
    override fun isNull(): Boolean  = source.isNull(getAndInvalidateColumnIndex())

    fun setColumnIndex(columnIndex: Int) {
        this.columnIndex = columnIndex
    }

    private fun getAndInvalidateColumnIndex(): Int {
        val result = when (columnIndex) {
            INVALID_COLUMN_INDEX -> throw DataProducerException(
                    "Calling #get without setting a column index"
            )

            else -> columnIndex
        }

        columnIndex = INVALID_COLUMN_INDEX
        return result
    }


    companion object {
        private const val INVALID_COLUMN_INDEX = -1
    }
}