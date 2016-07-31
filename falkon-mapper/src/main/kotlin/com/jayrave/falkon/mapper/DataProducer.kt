package com.jayrave.falkon.mapper

interface DataProducer {
    fun getShort(): Short
    fun getInt(): Int
    fun getLong(): Long
    fun getFloat(): Float
    fun getDouble(): Double
    fun getString(): String
    fun getBlob(): ByteArray
    fun isNull(): Boolean
}