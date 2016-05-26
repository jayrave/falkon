package com.jayrave.falkon.lib

import com.jayrave.falkon.engine.Sink
import java.util.*

internal class MapBackedSink : Sink {

    private val map = HashMap<String, Any?>()

    override fun put(columnName: String, value: Short) {
        map[columnName] = value
    }

    override fun put(columnName: String, value: Int) {
        map[columnName] = value
    }

    override fun put(columnName: String, value: Long) {
        map[columnName] = value
    }

    override fun put(columnName: String, value: Float) {
        map[columnName] = value
    }

    override fun put(columnName: String, value: Double) {
        map[columnName] = value
    }

    override fun put(columnName: String, value: String) {
        map[columnName] = value
    }

    override fun put(columnName: String, value: ByteArray) {
        map[columnName] = value
    }

    override fun putNull(columnName: String) {
        map[columnName] = null
    }

    fun sunkValues(): Map<String, Any?> {
        return map
    }
}