package com.jayrave.falkon

import com.jayrave.falkon.lib.StaticDataProducer
import com.jayrave.falkon.lib.ValueHoldingDataConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableCharConverterTest {

    private val converter = NullableCharConverter()

    @Test
    fun testFromWithNullValue() {
        val dataProducer = StaticDataProducer.createForChar(null)
        val producedValue = converter.from(dataProducer)
        assertThat(producedValue).isNull()
    }

    @Test
    fun testFromWithNonNullValue() {
        val inputValue = 'a'
        val dataProducer = StaticDataProducer.createForChar(inputValue)
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
        val inputValue = 'a'
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }
}