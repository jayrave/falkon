package com.jayrave.falkon.engine

/**
 * What happens on calling #put* multiple times for the same column name is
 * implementation dependent
 */
interface Sink {

    /**
     * The number of columns for which a value has been set
     */
    val size: Int

    fun put(columnName: String, value: Short)
    fun put(columnName: String, value: Int)
    fun put(columnName: String, value: Long)
    fun put(columnName: String, value: Float)
    fun put(columnName: String, value: Double)
    fun put(columnName: String, value: String)
    fun put(columnName: String, value: ByteArray)
    fun putNull(columnName: String)
}