package com.jayrave.falkon

interface NonNullDataProducer {
    fun getByte(): Byte
    fun getChar(): Char
    fun getShort(): Short
    fun getInt(): Int
    fun getLong(): Long
    fun getFloat(): Float
    fun getDouble(): Double
    fun getBoolean(): Boolean
    fun getString(): String
    fun getBlob(): ByteArray
}