package com.jayrave.falkon

import com.jayrave.falkon.lib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableIntConverterTest {

    private val converter = NullableIntConverter()

    @Test
    fun testFromWithNullValue() {
        val dataProducer = StaticDataProducer.createForInt(null)
        val producedValue = converter.from(dataProducer)
        assertThat(producedValue).isNull()
    }

    @Test
    fun testFromWithNonNullValue() {
        val inputValue = 1
        val dataProducer = StaticDataProducer.createForInt(inputValue)
        val producedValue = converter.from(dataProducer)
        assertThat(producedValue).isEqualTo(inputValue)
    }

    @Test
    fun testToWithNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to(null, consumer)
        assertThat(consumer.mostRecentConsumedValue).isNull()
    }

    @Test
    fun testToWithNonNullValue() {
        val inputValue = 1
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }
}