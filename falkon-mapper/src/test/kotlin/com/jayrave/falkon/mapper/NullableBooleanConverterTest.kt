package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.mapper.exceptions.ConversionException
import com.jayrave.falkon.mapper.testLib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableBooleanConverterTest {

    private val converter = NullableBooleanConverter()

    @Test
    fun testFromWithNullValue() {
        assertThat(testFromWithValue(null)).isNull()
    }

    @Test
    fun testFromWithNonNullValueForTrue() {
        assertThat(testFromWithValue(TRUE_VALUE)).isTrue()
    }

    @Test
    fun testFromWithNonNullValueForFalse() {
        assertThat(testFromWithValue(FALSE_VALUE)).isFalse()
    }

    @Test(expected = ConversionException::class)
    fun testFromWithInvalidPositiveNonNullValue() {
        testFromWithValue(2)
    }

    @Test(expected = ConversionException::class)
    fun testFromWithInvalidNegativeNonNullValue() {
        testFromWithValue(-1)
    }

    @Test
    fun testToWithNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to(null, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.SHORT))
    }

    @Test
    fun testToWithTrue() {
        assertThat(testToWithValue(true)).isEqualTo(TRUE_VALUE)
    }

    @Test
    fun testToWithFalse() {
        assertThat(testToWithValue(false)).isEqualTo(FALSE_VALUE)
    }

    private fun testFromWithValue(inputValue: Short?): Boolean? {
        val dataProducer = StaticDataProducer.createForShort(inputValue)
        return converter.from(dataProducer)
    }

    private fun testToWithValue(inputValue: Boolean?): Short? {
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        return consumer.mostRecentConsumedValue as Short?
    }


    companion object {
        private const val TRUE_VALUE: Short = 1
        private const val FALSE_VALUE: Short = 0
    }
}