package com.jayrave.falkon

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.testLib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullableBlobConverterTest {

    private val converter = NullableBlobConverter()

    @Test
    fun testFromWithNullValue() {
        val dataProducer = StaticDataProducer.createForBlob(null)
        val producedValue = converter.from(dataProducer)
        assertThat(producedValue).isNull()
    }

    @Test
    fun testFromWithNonNullValue() {
        val inputValue = byteArrayOf(1.toByte())
        val dataProducer = StaticDataProducer.createForBlob(inputValue)
        val producedValue = converter.from(dataProducer)
        assertThat(producedValue).isEqualTo(inputValue)
    }

    @Test
    fun testToWithNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to(null, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.BLOB))
    }

    @Test
    fun testToWithNonNullValue() {
        val inputValue = byteArrayOf(1.toByte())
        val consumer = ValueHoldingDataConsumer()
        converter.to(inputValue, consumer)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }
}