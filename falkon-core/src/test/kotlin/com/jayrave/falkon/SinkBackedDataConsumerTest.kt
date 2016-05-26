package com.jayrave.falkon

import com.jayrave.falkon.engine.Sink
import com.jayrave.falkon.exceptions.DataConsumerException
import com.jayrave.falkon.lib.MapBackedSink
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.assertj.core.data.MapEntry
import org.junit.Test

class SinkBackedDataConsumerTest {

    private val mapBackedSink = MapBackedSink()

    @Test
    fun testPutNonNullShort() {
        val inputValue: Short = 5.toShort()
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }
    
    @Test
    fun testPutNullShort() {
        buildConsumerAndSetColumnName().put(null as Short?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }
    
    @Test(expected = DataConsumerException::class)
    fun testPutShortWithoutColumnNameThrows() {
        buildConsumer().put(5.toShort())
    }
    
    @Test(expected = DataConsumerException::class)
    fun testPutShortResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(5.toShort()) }
        consumer.put(5.toShort())
    }

    @Test
    fun testPutNonNullInt() {
        val inputValue: Int = 5.toInt()
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullInt() {
        buildConsumerAndSetColumnName().put(null as Int?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutIntWithoutColumnNameThrows() {
        buildConsumer().put(5.toInt())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutIntResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(5.toInt()) }
        consumer.put(5.toInt())
    }

    @Test
    fun testPutNonNullLong() {
        val inputValue: Long = 5.toLong()
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullLong() {
        buildConsumerAndSetColumnName().put(null as Long?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutLongWithoutColumnNameThrows() {
        buildConsumer().put(5.toLong())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutLongResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(5.toLong()) }
        consumer.put(5.toLong())
    }

    @Test
    fun testPutNonNullFloat() {
        val inputValue: Float = 5.toFloat()
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullFloat() {
        buildConsumerAndSetColumnName().put(null as Float?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutFloatWithoutColumnNameThrows() {
        buildConsumer().put(5.toFloat())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutFloatResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(5.toFloat()) }
        consumer.put(5.toFloat())
    }

    @Test
    fun testPutNonNullDouble() {
        val inputValue: Double = 5.toDouble()
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullDouble() {
        buildConsumerAndSetColumnName().put(null as Double?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutDoubleWithoutColumnNameThrows() {
        buildConsumer().put(5.toDouble())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutDoubleResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(5.toDouble()) }
        consumer.put(5.toDouble())
    }

    @Test
    fun testPutNonNullString() {
        val inputValue: String = 5.toString()
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullString() {
        buildConsumerAndSetColumnName().put(null as String?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutStringWithoutColumnNameThrows() {
        buildConsumer().put(5.toString())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutStringResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(5.toString()) }
        consumer.put(5.toString())
    }

    @Test
    fun testPutNonNullBlob() {
        val inputValue: ByteArray = byteArrayOf(5)
        buildConsumerAndSetColumnName().put(inputValue)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullBlob() {
        buildConsumerAndSetColumnName().put(null as ByteArray?)
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutBlobWithoutColumnNameThrows() {
        buildConsumer().put(byteArrayOf(5))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutBlobResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.put(byteArrayOf(5)) }
        consumer.put(byteArrayOf(5))
    }

    @Test
    fun testPutNull() {
        buildConsumerAndSetColumnName().putNull()
        assertThat(mapBackedSink.sunkValues()).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, null))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutNullWithoutColumnNameThrows() {
        buildConsumer().putNull()
    }

    @Test(expected = DataConsumerException::class)
    fun testPutNullResetsColumnName() {
        val consumer = buildConsumerAndSetColumnName()
        failIfThrows { consumer.putNull() }
        consumer.putNull()
    }

    private fun buildConsumer(): SinkBackedDataConsumer<Sink> {
        return SinkBackedDataConsumer(mapBackedSink)
    }

    private fun buildConsumerAndSetColumnName(): SinkBackedDataConsumer<Sink> {
        val consumer = buildConsumer()
        consumer.setColumnName(DEFAULT_COLUMN_NAME)
        return consumer
    }

    private fun failIfThrows(func: () -> Any?) {
        try {
            func.invoke()
        } catch (e: Exception) {
            fail("This isn't supposed to happen!!")
        }
    }


    companion object {
        private const val DEFAULT_COLUMN_NAME = "test"
    }
}