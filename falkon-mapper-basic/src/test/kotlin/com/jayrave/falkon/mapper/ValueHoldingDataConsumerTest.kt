package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ValueHoldingDataConsumerTest {

    private val consumer = ValueHoldingDataConsumer()

    @Test
    fun testPutNullShort() {
        consumer.put(null as Short?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.SHORT))
    }

    @Test
    fun testPutNonNullShort() {
        val inputValue = 1.toShort()
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNullInt() {
        consumer.put(null as Int?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.INT))
    }

    @Test
    fun testPutNonNullInt() {
        val inputValue = 1.toInt()
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNullLong() {
        consumer.put(null as Long?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.LONG))
    }

    @Test
    fun testPutNonNullLong() {
        val inputValue = 1.toLong()
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNullFloat() {
        consumer.put(null as Float?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.FLOAT))
    }

    @Test
    fun testPutNonNullFloat() {
        val inputValue = 1.toFloat()
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNullDouble() {
        consumer.put(null as Double?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.DOUBLE))
    }

    @Test
    fun testPutNonNullDouble() {
        val inputValue = 1.toDouble()
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNullString() {
        consumer.put(null as String?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.STRING))
    }

    @Test
    fun testPutNonNullString() {
        val inputValue = 1.toString()
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNullBlob() {
        consumer.put(null as ByteArray?)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.BLOB))
    }

    @Test
    fun testPutNonNullBlob() {
        val inputValue = byteArrayOf(1)
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }
}