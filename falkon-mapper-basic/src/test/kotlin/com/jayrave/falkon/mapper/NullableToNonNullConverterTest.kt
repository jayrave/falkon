package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.testLib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableToNonNullConverterTest {

    @Test
    fun testDbTypeIsDelegated() {
        val intConverter = NullableToNonNullConverter(NullableIntConverter())
        val stringConverter = NullableToNonNullConverter(NullableStringConverter())

        assertThat(intConverter.dbType).isEqualTo(Type.INT)
        assertThat(stringConverter.dbType).isEqualTo(Type.STRING)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun testFromWithNullValueThrows() {
        val dataProducer = StaticDataProducer.createForString(null)
        NullableToNonNullConverter(NullableStringConverter()).from(dataProducer)
    }

    @Test
    fun testFromWithNonNullValue() {
        val inputValue = "full counter"
        val dataProducer = StaticDataProducer.createForString(inputValue)
        val nullableConverter = NullableStringConverter()
        val nonNullConverter = NullableToNonNullConverter(nullableConverter)

        val valueProducedByNullableConverter = nullableConverter.from(dataProducer)
        val valueProducedByNonNullConverter = nonNullConverter.from(dataProducer)

        assertThat(valueProducedByNonNullConverter).isNotNull()
        assertThat(valueProducedByNonNullConverter).isEqualTo(valueProducedByNullableConverter)
    }

    @Test
    fun testToWithNonNullValue() {
        val inputValue = "dark nebula"
        val consumerForNullableConverter = ValueHoldingDataConsumer()
        val consumerForNonNullConverter = ValueHoldingDataConsumer()
        val nullableConverter = NullableStringConverter()
        val nonNullConverter = NullableToNonNullConverter(nullableConverter)

        nullableConverter.to(inputValue, consumerForNullableConverter)
        nonNullConverter.to(inputValue, consumerForNonNullConverter)

        assertThat(consumerForNonNullConverter.mostRecentConsumedValue).isNotNull()
        assertThat(consumerForNonNullConverter.mostRecentConsumedValue)
                .isEqualTo(consumerForNullableConverter.mostRecentConsumedValue)
    }
}