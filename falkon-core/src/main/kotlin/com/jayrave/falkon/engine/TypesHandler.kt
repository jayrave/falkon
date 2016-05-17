package com.jayrave.falkon.engine

interface TypesHandler {
    fun byteHandler(): TypeHandler<Byte>
    fun charHandler(): TypeHandler<Char>
    fun shortHandler(): TypeHandler<Short>
    fun intHandler(): TypeHandler<Int>
    fun longHandler(): TypeHandler<Long>
    fun floatHandler(): TypeHandler<Float>
    fun doubleHandler(): TypeHandler<Double>
    fun booleanHandler(): TypeHandler<Boolean>
    fun stringHandler(): TypeHandler<String>
    fun blobHandler(): TypeHandler<ByteArray>
}