package com.jayrave.falkon

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullHandlingNullableDataConsumerTest {

    private val consumer = ValueHoldingDataConsumer()

    @Test
    fun testPutWithNullShort() {
        consumer.put(null as Short?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullShort() {
        consumer.put(1.toShort())
        assertInteractionsForPuttingNonNullValue(Short::class.javaObjectType)
    }

    @Test
    fun testPutWithNullInt() {
        consumer.put(null as Int?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullInt() {
        consumer.put(1.toInt())
        assertInteractionsForPuttingNonNullValue(Int::class.javaObjectType)
    }

    @Test
    fun testPutWithNullLong() {
        consumer.put(null as Long?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullLong() {
        consumer.put(1.toLong())
        assertInteractionsForPuttingNonNullValue(Long::class.javaObjectType)
    }

    @Test
    fun testPutWithNullFloat() {
        consumer.put(null as Float?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullFloat() {
        consumer.put(1.toFloat())
        assertInteractionsForPuttingNonNullValue(Float::class.javaObjectType)
    }

    @Test
    fun testPutWithNullDouble() {
        consumer.put(null as Double?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullDouble() {
        consumer.put(1.toDouble())
        assertInteractionsForPuttingNonNullValue(Double::class.javaObjectType)
    }

    @Test
    fun testPutWithNullString() {
        consumer.put(null as String?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullString() {
        consumer.put("1")
        assertInteractionsForPuttingNonNullValue(String::class.java)
    }

    @Test
    fun testPutWithNullBlob() {
        consumer.put(null as ByteArray?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullBlob() {
        consumer.put(byteArrayOf(1))
        assertInteractionsForPuttingNonNullValue(ByteArray::class.java)
    }

    private fun assertInteractionsForPuttingNullValue() {
        assertThat(consumer.mostRecentConsumedValue).isNull()
    }

    private fun assertInteractionsForPuttingNonNullValue(type: Class<*>) {
        assertThat(consumer.mostRecentConsumedValue).isExactlyInstanceOf(type)
    }
}