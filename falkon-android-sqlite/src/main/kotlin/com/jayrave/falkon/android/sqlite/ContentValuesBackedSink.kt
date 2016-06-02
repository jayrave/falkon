package com.jayrave.falkon.android.sqlite

import android.content.ContentValues
import com.jayrave.falkon.engine.Sink

/**
 * Calling #put* multiple times for the same column name overwrites existing value
 */
class ContentValuesBackedSink : Sink {

    /**
     * When accessed from outside this class, this should be treated as read-only
     */
    val contentValues = ContentValues()

    override val size: Int
        get() = contentValues.size()

    override fun put(columnName: String, value: Short) = contentValues.put(columnName, value)
    override fun put(columnName: String, value: Int) = contentValues.put(columnName, value)
    override fun put(columnName: String, value: Long) = contentValues.put(columnName, value)
    override fun put(columnName: String, value: Float) = contentValues.put(columnName, value)
    override fun put(columnName: String, value: Double) = contentValues.put(columnName, value)
    override fun put(columnName: String, value: String) = contentValues.put(columnName, value)
    override fun put(columnName: String, value: ByteArray) = contentValues.put(columnName, value)
    override fun putNull(columnName: String) = contentValues.putNull(columnName)
}