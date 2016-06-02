package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.lib.MapBackedSource
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.util.*

class SourceBackedDataProducerTest {

    @Test
    fun testGetNonNullShort() {
        val inputValue: Short = 5.toShort()
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getShort()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testGetNonNullInt() {
        val inputValue: Int = 5.toInt()
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getInt()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testGetNonNullLong() {
        val inputValue: Long = 5.toLong()
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getLong()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testGetNonNullFloat() {
        val inputValue: Float = 5.toFloat()
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getFloat()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testGetNonNullDouble() {
        val inputValue: Double = 5.toDouble()
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getDouble()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testGetNonNullString() {
        val inputValue: String = 5.toString()
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getString()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testGetNonNullBlob() {
        val inputValue: ByteArray = byteArrayOf(5)
        val actualValue = buildProducerAndSetColumnIndex(inputValue).getBlob()
        assertThat(actualValue).isEqualTo(inputValue)
    }

    @Test
    fun testIsNull() {
        assertThat(buildProducerAndSetColumnIndex(null).isNull()).isTrue()
    }


    private fun buildProducerAndSetColumnIndex(inputValue: Any?): SourceBackedDataProducer {
        // Add a few dummy columns to make sure that everything works even when
        // there are multiple columns

        val map: Map<String, Any?> = mapOf(
                DEFAULT_COLUMN_NAME to inputValue,
                UUID.randomUUID().toString() to UUID.randomUUID(),
                UUID.randomUUID().toString() to UUID.randomUUID()
        )

        val source = MapBackedSource(map)
        val producer = SourceBackedDataProducer(source)
        producer.setColumnIndex(source.getColumnIndex(DEFAULT_COLUMN_NAME))
        return producer
    }


    companion object {
        private const val DEFAULT_COLUMN_NAME = "test"
    }
}