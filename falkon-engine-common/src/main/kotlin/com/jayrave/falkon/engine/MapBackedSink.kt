package com.jayrave.falkon.engine

import java.util.*

/**
 * Calling #put* multiple times for the same column name overwrites existing value
 */
class MapBackedSink : Sink {

    private val mutableMap = HashMap<String, Any?>()
    val map: Map<String, Any?> = mutableMap

    override val size: Int
        get() = mutableMap.size

    override fun put(columnName: String, value: Short) {
        mutableMap[columnName] = value
    }

    override fun put(columnName: String, value: Int) {
        mutableMap[columnName] = value
    }

    override fun put(columnName: String, value: Long) {
        mutableMap[columnName] = value
    }

    override fun put(columnName: String, value: Float) {
        mutableMap[columnName] = value
    }

    override fun put(columnName: String, value: Double) {
        mutableMap[columnName] = value
    }

    override fun put(columnName: String, value: String) {
        mutableMap[columnName] = value
    }

    override fun put(columnName: String, value: ByteArray) {
        mutableMap[columnName] = value
    }

    override fun putNull(columnName: String) {
        mutableMap[columnName] = null
    }
}