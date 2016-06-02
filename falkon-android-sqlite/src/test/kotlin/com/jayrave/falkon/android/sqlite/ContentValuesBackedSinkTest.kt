package com.jayrave.falkon.android.sqlite

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ContentValuesBackedSinkTest : RobolectricTestBaseClass() {

    private val sink = ContentValuesBackedSink()

    @Test
    fun testSize() {
        assertThat(sink.size).isEqualTo(0)
        sink.put("test", 5)
        assertThat(sink.size).isEqualTo(1)
    }

    @Test
    fun testPutShort() {
        val columnName = "test"
        val value = 5.toShort()
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsShort(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutShortOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toShort()
        val overwritingValue = 6.toShort()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsShort(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutInt() {
        val columnName = "test"
        val value = 5.toInt()
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsInteger(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutIntOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toInt()
        val overwritingValue = 6.toInt()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsInteger(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutLong() {
        val columnName = "test"
        val value = 5.toLong()
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsLong(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutLongOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toLong()
        val overwritingValue = 6.toLong()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsLong(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutFloat() {
        val columnName = "test"
        val value = 5.toFloat()
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsFloat(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutFloatOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toFloat()
        val overwritingValue = 6.toFloat()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsFloat(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutDouble() {
        val columnName = "test"
        val value = 5.toDouble()
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsDouble(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutDoubleOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = 5.toDouble()
        val overwritingValue = 6.toDouble()
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsDouble(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutString() {
        val columnName = "test"
        val value = "ha ha ha"
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsString(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutStringOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = "ha ha ha"
        val overwritingValue = "ho ho ho"
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsString(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutBlob() {
        val columnName = "test"
        val value = byteArrayOf(5)
        sink.put(columnName, value)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsByteArray(columnName)).isEqualTo(value)
    }

    @Test
    fun testPutBlobOverwritesExistingValue() {
        val columnName = "test"
        val existingValue = byteArrayOf(5)
        val overwritingValue = byteArrayOf(6)
        sink.put(columnName, existingValue)
        sink.put(columnName, overwritingValue)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.getAsByteArray(columnName)).isEqualTo(overwritingValue)
    }

    @Test
    fun testPutNull() {
        val columnName = "test"
        sink.putNull(columnName)
        assertThat(sink.contentValues.size()).isEqualTo(1)
        assertThat(sink.contentValues.get(columnName)).isNull()
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

        assertThat(sink.contentValues.size()).isEqualTo(7)
        assertThat(sink.contentValues.get(shortColumnName)).isNull()
        assertThat(sink.contentValues.get(intColumnName)).isNull()
        assertThat(sink.contentValues.get(longColumnName)).isNull()
        assertThat(sink.contentValues.get(floatColumnName)).isNull()
        assertThat(sink.contentValues.get(doubleColumnName)).isNull()
        assertThat(sink.contentValues.get(stringColumnName)).isNull()
        assertThat(sink.contentValues.get(blobColumnName)).isNull()
    }
}