package com.jayrave.falkon

import com.jayrave.falkon.exceptions.ConversionException
import com.jayrave.falkon.lib.StaticDataProducer
import com.jayrave.falkon.lib.ValueHoldingDataConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableByteConverterTest {

    private val converter = NullableByteConverter()

    @Test
    fun testFromWithNullValue() {
        assertThat(testFromWithValue(null)).isNull()
    }

    @Test
    fun testFromWithValidPositiveValue() {
        val inputValue: Short = 1
        assertThat(testFromWithValue(inputValue)).isEqualTo(inputValue.toByte())
    }

    @Test
    fun testFromWithValidNegativeValue() {
        val inputValue: Short = -1
        assertThat(testFromWithValue(inputValue)).isEqualTo(inputValue.toByte())
    }

    @Test(expected = ConversionException::class)
    fun testFromWithInvalidPositiveValue() {
        testFromWithValue(128)
    }

    @Test(expected = ConversionException::class)
    fun testFromWithInvalidNegativeValue() {
        testFromWithValue(-129)
    }

    @Test
    fun testToWithNullValue() {
        assertThat(testToWithValue(null)).isNull()
    }

    @Test
    fun testToWithNonNullValue() {
        val inputValue = 1.toByte()
        assertThat(testToWithValue(inputValue)).isEqualTo(inputValue.toShort())
    }

    private fun testFromWithValue(inputValue: Short?): Byte? {
        val dataProducer = StaticDataProducer.createForShort(inputValue)
        return converter.from(dataProducer)
    }

    private fun testToWithValue(inputValue: Byte?): Short? {
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        return consumer.mostRecentConsumedValue as Short?
    }
}