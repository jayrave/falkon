package com.jayrave.falkon

import com.jayrave.falkon.lib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableToNonNullConverterTest {

    private val nullableConverter = NullableStringConverter()
    private val nonNullConverter = NullableToNonNullConverter(nullableConverter)

    @Test(expected = KotlinNullPointerException::class)
    fun testFromWithNullValueThrows() {
        val dataProducer = StaticDataProducer.createForString(null)
        nonNullConverter.from(dataProducer)
    }

    @Test
    fun testFromWithNonNullValue() {
        val inputValue = "full counter"
        val dataProducer = StaticDataProducer.createForString(inputValue)

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

        nullableConverter.to(inputValue, consumerForNullableConverter)
        nonNullConverter.to(inputValue, consumerForNonNullConverter)

        assertThat(consumerForNonNullConverter.mostRecentConsumedValue).isNotNull()
        assertThat(consumerForNonNullConverter.mostRecentConsumedValue)
                .isEqualTo(consumerForNullableConverter.mostRecentConsumedValue)
    }
}