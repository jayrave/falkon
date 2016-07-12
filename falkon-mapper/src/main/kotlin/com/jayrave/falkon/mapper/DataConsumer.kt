package com.jayrave.falkon.mapper

interface DataConsumer {
    fun put(short: Short?)
    fun put(int: Int?)
    fun put(long: Long?)
    fun put(float: Float?)
    fun put(double: Double?)
    fun put(string: String?)
    fun put(blob: ByteArray?)
}