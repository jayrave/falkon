package com.jayrave.falkon

interface NullableDataConsumer {
    fun put(byte: Byte)
    fun put(byte: Byte?)
    fun put(char: Char)
    fun put(char: Char?)
    fun put(short: Short)
    fun put(short: Short?)
    fun put(int: Int)
    fun put(int: Int?)
    fun put(long: Long)
    fun put(long: Long?)
    fun put(float: Float)
    fun put(float: Float?)
    fun put(double: Double)
    fun put(double: Double?)
    fun put(boolean: Boolean)
    fun put(boolean: Boolean?)
    fun put(string: String?)
    fun put(blob: ByteArray?)
    fun putNull()
}