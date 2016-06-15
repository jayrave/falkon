package com.jayrave.falkon

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.exceptions.ConversionException
import com.jayrave.falkon.testLib.StaticDataProducer
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
        val consumer = ValueHoldingDataConsumer()
        converter.to(null, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.SHORT))
    }

    @Test
    fun testToWithNonNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to(1, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(1.toShort())
    }

    private fun testFromWithValue(inputValue: Short?): Byte? {
        val dataProducer = StaticDataProducer.createForShort(inputValue)
        return converter.from(dataProducer)
    }
}