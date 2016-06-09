package com.jayrave.falkon

import com.jayrave.falkon.exceptions.ConversionException
import com.jayrave.falkon.testLib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableCharConverterTest {

    private val converter = NullableCharConverter()

    @Test
    fun testFromWithNullValue() {
        assertThat(testFromWithValue(null)).isNull()
    }

    @Test
    fun testFromWithValidNonNullValue() {
        val inputValue = "a"
        assertThat(testFromWithValue(inputValue)).isEqualTo(inputValue.single())
    }

    @Test(expected = ConversionException::class)
    fun testFromWithInvalidNonNullValue() {
        val inputValue = "ab"
        testFromWithValue(inputValue)
    }

    @Test
    fun testToWithNullValue() {
        assertThat(testToWithValue(null)).isNull()
    }

    @Test
    fun testToWithNonNullValue() {
        val inputValue = 'a'
        assertThat(testToWithValue(inputValue)).isEqualTo(inputValue.toString())
    }

    private fun testFromWithValue(inputValue: String?): Char? {
        val dataProducer = StaticDataProducer.createForString(inputValue)
        return converter.from(dataProducer)
    }

    private fun testToWithValue(inputValue: Char?): String? {
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        return consumer.mostRecentConsumedValue as String?
    }
}