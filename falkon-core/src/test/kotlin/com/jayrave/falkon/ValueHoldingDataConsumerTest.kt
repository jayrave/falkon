package com.jayrave.falkon

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ValueHoldingDataConsumerTest {

    private val consumer = ValueHoldingDataConsumer()

    @Test
    fun testPutNullShort() {
        consumer.put(null as Short?)
        assertThat(consumer.mostRecentConsumedValue).isNull()
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
        assertThat(consumer.mostRecentConsumedValue).isNull()
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
        assertThat(consumer.mostRecentConsumedValue).isNull()
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
        assertThat(consumer.mostRecentConsumedValue).isNull()
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
        assertThat(consumer.mostRecentConsumedValue).isNull()
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
        assertThat(consumer.mostRecentConsumedValue).isNull()
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
        assertThat(consumer.mostRecentConsumedValue).isNull()
    }

    @Test
    fun testPutNonNullBlob() {
        val inputValue = byteArrayOf(1)
        consumer.put(inputValue)
        assertThat(consumer.mostRecentConsumedValue).isEqualTo(inputValue)
    }

    @Test
    fun testPutNull() {
        consumer.putNull()
        assertThat(consumer.mostRecentConsumedValue).isNull()
    }
}