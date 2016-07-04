package com.jayrave.falkon.dao

import com.jayrave.falkon.DataProducer
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.exceptions.DataProducerException

/**
 * A [DataProducer] that gets the required value from the underlying [Source]. Update the
 * column index of [Source] to talk with as & when required via [setColumnIndex]
 */
internal class SourceBackedDataProducer(val source: Source) : DataProducer {

    private var columnIndex: Int = INVALID_COLUMN_INDEX

    override fun getBlob(): ByteArray?  = source.getBlob(getValidColumnIndex())
    override fun getDouble(): Double?  = source.getDouble(getValidColumnIndex())
    override fun getFloat(): Float?  = source.getFloat(getValidColumnIndex())
    override fun getInt(): Int?  = source.getInt(getValidColumnIndex())
    override fun getLong(): Long?  = source.getLong(getValidColumnIndex())
    override fun getShort(): Short?  = source.getShort(getValidColumnIndex())
    override fun getString(): String?  = source.getString(getValidColumnIndex())
    override fun isNull(): Boolean  = source.isNull(getValidColumnIndex())

    fun setColumnIndex(columnIndex: Int) {
        this.columnIndex = columnIndex
    }

    private fun getValidColumnIndex(): Int {
        return when {
            columnIndex != INVALID_COLUMN_INDEX -> columnIndex
            else -> throw DataProducerException("Calling #get without setting a column index")
        }
    }


    companion object {
        private const val INVALID_COLUMN_INDEX = -1
    }
}