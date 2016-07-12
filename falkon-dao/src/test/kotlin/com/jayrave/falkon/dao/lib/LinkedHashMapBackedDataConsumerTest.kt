package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.mapper.exceptions.DataConsumerException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.assertj.core.data.MapEntry
import org.junit.Test

class LinkedHashMapBackedDataConsumerTest {

    @Test
    fun testPutNonNullShort() {
        val inputValue: Short = 5.toShort()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }
    
    @Test
    fun testPutNullShort() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Short?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.SHORT))
        )
    }
    
    @Test(expected = DataConsumerException::class)
    fun testPutShortWithoutColumnNameThrows() {
        buildConsumer().put(5.toShort())
    }
    
    @Test(expected = DataConsumerException::class)
    fun testPutShortResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toShort()) }
        dataConsumer.put(5.toShort())
    }

    @Test
    fun testPutNonNullInt() {
        val inputValue: Int = 5.toInt()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullInt() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Int?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.INT))
        )
    }

    @Test(expected = DataConsumerException::class)
    fun testPutIntWithoutColumnNameThrows() {
        buildConsumer().put(5.toInt())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutIntResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toInt()) }
        dataConsumer.put(5.toInt())
    }

    @Test
    fun testPutNonNullLong() {
        val inputValue: Long = 5.toLong()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullLong() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Long?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.LONG))
        )
    }

    @Test(expected = DataConsumerException::class)
    fun testPutLongWithoutColumnNameThrows() {
        buildConsumer().put(5.toLong())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutLongResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toLong()) }
        dataConsumer.put(5.toLong())
    }

    @Test
    fun testPutNonNullFloat() {
        val inputValue: Float = 5.toFloat()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullFloat() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Float?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.FLOAT))
        )
    }

    @Test(expected = DataConsumerException::class)
    fun testPutFloatWithoutColumnNameThrows() {
        buildConsumer().put(5.toFloat())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutFloatResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toFloat()) }
        dataConsumer.put(5.toFloat())
    }

    @Test
    fun testPutNonNullDouble() {
        val inputValue: Double = 5.toDouble()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullDouble() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Double?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.DOUBLE))
        )
    }

    @Test(expected = DataConsumerException::class)
    fun testPutDoubleWithoutColumnNameThrows() {
        buildConsumer().put(5.toDouble())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutDoubleResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toDouble()) }
        dataConsumer.put(5.toDouble())
    }

    @Test
    fun testPutNonNullString() {
        val inputValue: String = 5.toString()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullString() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as String?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.STRING))
        )
    }

    @Test(expected = DataConsumerException::class)
    fun testPutStringWithoutColumnNameThrows() {
        buildConsumer().put(5.toString())
    }

    @Test(expected = DataConsumerException::class)
    fun testPutStringResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toString()) }
        dataConsumer.put(5.toString())
    }

    @Test
    fun testPutNonNullBlob() {
        val inputValue: ByteArray = byteArrayOf(5)
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }

    @Test
    fun testPutNullBlob() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as ByteArray?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.BLOB))
        )
    }

    @Test(expected = DataConsumerException::class)
    fun testPutBlobWithoutColumnNameThrows() {
        buildConsumer().put(byteArrayOf(5))
    }

    @Test(expected = DataConsumerException::class)
    fun testPutBlobResetsColumnName() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(byteArrayOf(5)) }
        dataConsumer.put(byteArrayOf(5))
    }

    private fun buildConsumer(): LinkedHashMapBackedDataConsumer {
        return LinkedHashMapBackedDataConsumer()
    }

    private fun buildConsumerAndSetColumnName(): LinkedHashMapBackedDataConsumer {
        val dataConsumer = LinkedHashMapBackedDataConsumer()
        dataConsumer.setColumnName(DEFAULT_COLUMN_NAME)
        return dataConsumer
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