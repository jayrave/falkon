package com.jayrave.falkon

import com.jayrave.falkon.lib.StaticDataProducer
import com.jayrave.falkon.lib.ValueHoldingDataConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableStringConverterTest {

    private val converter = NullableStringConverter()

    @Test
    fun testFromWithNullValue() {
        val dataProducer = StaticDataProducer.createForString(null)
        val producedValue = converter.from(dataProducer)
        assertThat(producedValue).isNull()
    }

    @Test
    fun testFromWithNonNullValue() {
        val inputValue = "ha ha ha"
        val dataProducer = StaticDataProducer.createForString(inputValue)
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
        val inputValue = "magic"
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }
}