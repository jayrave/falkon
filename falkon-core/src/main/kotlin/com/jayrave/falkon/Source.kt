package com.jayrave.falkon

interface Source {
    fun getColumnIndex(columnName: String): Int
    fun getByte(columnIndex: Int): Byte
    fun getChar(columnIndex: Int): Char
    fun getShort(columnIndex: Int): Short
    fun getInt(columnIndex: Int): Int
    fun getLong(columnIndex: Int): Long
    fun getFloat(columnIndex: Int): Float
    fun getDouble(columnIndex: Int): Double
    fun getBoolean(columnIndex: Int): Boolean
    fun getString(columnIndex: Int): String
    fun getBlob(columnIndex: Int): ByteArray
    fun isNull(columnIndex: Int): Boolean
}