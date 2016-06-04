package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.*

import org.junit.Test

class MapBackedSinkTest {

    private val sink = MapBackedSink()

    @Test
    fun testSize() {
        assertThat(sink.size).isEqualTo(0)
        sink.put("test_1", 5)
        assertThat(sink.size).isEqualTo(1)
        sink.put("test_2", "5")
        sink.put("test_3", byteArrayOf(5))
        assertThat(sink.size).isEqualTo(3)
    }

    @Test
    fun testPutShort() {
        val columnName = "test"
        val value = 5.toShort()
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutShortOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toShort()
        val overwritingValue = 6.toShort()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutInt() {
        val columnName = "test"
        val value = 5.toInt()
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutIntOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toInt()
        val overwritingValue = 6.toInt()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutLong() {
        val columnName = "test"
        val value = 5.toLong()
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutLongOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toLong()
        val overwritingValue = 6.toLong()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutFloat() {
        val columnName = "test"
        val value = 5.toFloat()
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutFloatOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toFloat()
        val overwritingValue = 6.toFloat()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutDouble() {
        val columnName = "test"
        val value = 5.toDouble()
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutDoubleOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toDouble()
        val overwritingValue = 6.toDouble()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutString() {
        val columnName = "test"
        val value = "ha ha ha"
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutStringOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = "ha ha ha"
        val overwritingValue = "ho ho ho"
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutBlob() {
        val columnName = "test"
        val value = byteArrayOf(5)
        sink.put(columnName, value)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(value)
    }

    @Test
    fun testPutBlobOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = byteArrayOf(5)
        val overwritingValue = byteArrayOf(6)
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutNull() {
        val columnName = "test"
        sink.putNull(columnName)
        assertThat(sink.map.size).isEqualTo(1)
        assertThat(sink.map[columnName]).isNull()
    }

    @Test
    fun testPutNullOverwritesExistingValue() {
        val shortColumnName = "short"
        val intColumnName = "int"
        val longColumnName = "long"
        val floatColumnName = "float"
        val doubleColumnName = "double"
        val stringColumnName = "string"
        val blobColumnName = "blob"

        sink.put(shortColumnName, 5.toShort())
        sink.put(intColumnName, 5.toInt())
        sink.put(longColumnName, 5.toLong())
        sink.put(floatColumnName, 5.toFloat())
        sink.put(doubleColumnName, 5.toDouble())
        sink.put(stringColumnName, 5.toString())
        sink.put(blobColumnName, byteArrayOf(5))

        sink.putNull(shortColumnName)
        sink.putNull(intColumnName)
        sink.putNull(longColumnName)
        sink.putNull(floatColumnName)
        sink.putNull(doubleColumnName)
        sink.putNull(stringColumnName)
        sink.putNull(blobColumnName)

        assertThat(sink.map.size).isEqualTo(7)
        assertThat(sink.map[shortColumnName]).isNull()
        assertThat(sink.map[intColumnName]).isNull()
        assertThat(sink.map[longColumnName]).isNull()
        assertThat(sink.map[floatColumnName]).isNull()
        assertThat(sink.map[doubleColumnName]).isNull()
        assertThat(sink.map[stringColumnName]).isNull()
        assertThat(sink.map[blobColumnName]).isNull()
    }
}