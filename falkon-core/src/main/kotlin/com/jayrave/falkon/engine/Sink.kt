package com.jayrave.falkon.engine

interface Sink {
    fun put(columnName: String, value: Byte)
    fun put(columnName: String, value: Char)
    fun put(columnName: String, value: Short)
    fun put(columnName: String, value: Int)
    fun put(columnName: String, value: Long)
    fun put(columnName: String, value: Float)
    fun put(columnName: String, value: Double)
    fun put(columnName: String, value: Boolean)
    fun put(columnName: String, value: String)
    fun put(columnName: String, value: ByteArray)
    fun putNull(columnName: String)
}