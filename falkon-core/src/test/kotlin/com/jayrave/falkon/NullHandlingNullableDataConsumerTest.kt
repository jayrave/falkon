package com.jayrave.falkon

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NullHandlingNullableDataConsumerTest {

    private val consumer = NullHandlingNullableDataConsumerForTest()

    @Test
    fun testPutWithNullByte() {
        consumer.put(null as Byte?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullByte() {
        consumer.put(1.toByte())
        assertInteractionsForPuttingNonNullValue(Byte::class.javaObjectType)
    }

    @Test
    fun testPutWithNullChar() {
        consumer.put(null as Char?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullChar() {
        consumer.put(1.toChar())
        assertInteractionsForPuttingNonNullValue(Char::class.javaObjectType)
    }

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
    fun testPutWithNullBoolean() {
        consumer.put(null as Boolean?)
        assertInteractionsForPuttingNullValue()
    }

    @Test
    fun testPutWithNonNullBoolean() {
        consumer.put(true)
        assertInteractionsForPuttingNonNullValue(Boolean::class.javaObjectType)
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
        assertThat(consumer.numberOfTimesConsumedValueSet).isEqualTo(1)
        assertThat(consumer.consumedValue).isNull()
    }

    private fun assertInteractionsForPuttingNonNullValue(type: Class<*>) {
        assertThat(consumer.numberOfTimesConsumedValueSet).isEqualTo(1)
        assertThat(consumer.consumedValue).isExactlyInstanceOf(type)
    }



    private class NullHandlingNullableDataConsumerForTest : NullHandlingNullableDataConsumer() {

        var numberOfTimesConsumedValueSet: Int = 0
        var consumedValue: Any? = null
            private set(value) {
                numberOfTimesConsumedValueSet++
                field = value
            }

        override fun put(byte: Byte) { consumedValue = byte }
        override fun put(char: Char) { consumedValue = char }
        override fun put(short: Short) { consumedValue = short }
        override fun put(int: Int) { consumedValue = int }
        override fun put(long: Long) { consumedValue = long }
        override fun put(float: Float) { consumedValue = float }
        override fun put(double: Double) { consumedValue = double }
        override fun put(boolean: Boolean) { consumedValue = boolean }
        override fun putNonNullString(string: String) { consumedValue = string }
        override fun putNonNullBlob(blob: ByteArray) { consumedValue = blob }
        override fun putNull() { consumedValue = null }
    }
}