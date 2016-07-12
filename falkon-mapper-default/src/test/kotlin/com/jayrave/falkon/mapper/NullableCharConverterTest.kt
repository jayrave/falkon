package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.mapper.exceptions.ConversionException
import com.jayrave.falkon.mapper.testLib.StaticDataProducer
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
        val consumer = ValueHoldingDataConsumer()
        converter.to(null, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.STRING))
    }

    @Test
    fun testToWithNonNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to('a', consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo("a")
    }

    private fun testFromWithValue(inputValue: String?): Char? {
        val dataProducer = StaticDataProducer.createForString(inputValue)
        return converter.from(dataProducer)
    }
}