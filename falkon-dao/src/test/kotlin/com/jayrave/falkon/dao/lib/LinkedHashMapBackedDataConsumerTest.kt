package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.assertj.core.data.MapEntry
import org.junit.Test

class LinkedHashMapBackedDataConsumerTest {

    @Test
    fun `test put non null short`() {
        val inputValue: Short = 5.toShort()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null short`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Short?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.SHORT))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put short without column name throws`() {
        buildConsumer().put(5.toShort())
    }


    @Test(expected = RuntimeException::class)
    fun `test put short resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toShort()) }
        dataConsumer.put(5.toShort())
    }


    @Test
    fun `test put non null int`() {
        val inputValue: Int = 5
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null int`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Int?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.INT))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put int without column name throws`() {
        buildConsumer().put(5.toInt())
    }


    @Test(expected = RuntimeException::class)
    fun `test put int resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toInt()) }
        dataConsumer.put(5.toInt())
    }


    @Test
    fun `test put non null long`() {
        val inputValue: Long = 5.toLong()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null long`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Long?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.LONG))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put long without column name throws`() {
        buildConsumer().put(5.toLong())
    }


    @Test(expected = RuntimeException::class)
    fun `test put long resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toLong()) }
        dataConsumer.put(5.toLong())
    }


    @Test
    fun `test put non null float`() {
        val inputValue: Float = 5.toFloat()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null float`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Float?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.FLOAT))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put float without column name throws`() {
        buildConsumer().put(5.toFloat())
    }


    @Test(expected = RuntimeException::class)
    fun `test put float resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toFloat()) }
        dataConsumer.put(5.toFloat())
    }


    @Test
    fun `test put non null double`() {
        val inputValue: Double = 5.toDouble()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null double`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as Double?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.DOUBLE))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put double without column name throws`() {
        buildConsumer().put(5.toDouble())
    }


    @Test(expected = RuntimeException::class)
    fun `test put double resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toDouble()) }
        dataConsumer.put(5.toDouble())
    }


    @Test
    fun `test put non null string`() {
        val inputValue: String = 5.toString()
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null string`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as String?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.STRING))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put string without column name throws`() {
        buildConsumer().put(5.toString())
    }


    @Test(expected = RuntimeException::class)
    fun `test put string resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(5.toString()) }
        dataConsumer.put(5.toString())
    }


    @Test
    fun `test put non null blob`() {
        val inputValue: ByteArray = byteArrayOf(5)
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(inputValue)
        assertThat(dataConsumer.map).containsOnly(MapEntry.entry(DEFAULT_COLUMN_NAME, inputValue))
    }


    @Test
    fun `test put null blob`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        dataConsumer.put(null as ByteArray?)
        assertThat(dataConsumer.map).containsOnly(
                MapEntry.entry(DEFAULT_COLUMN_NAME, TypedNull(Type.BLOB))
        )
    }


    @Test(expected = RuntimeException::class)
    fun `test put blob without column name throws`() {
        buildConsumer().put(byteArrayOf(5))
    }


    @Test(expected = RuntimeException::class)
    fun `test put blob resets column name`() {
        val dataConsumer = buildConsumerAndSetColumnName()
        failIfThrows { dataConsumer.put(byteArrayOf(5)) }
        dataConsumer.put(byteArrayOf(5))
    }



    companion object {
        private const val DEFAULT_COLUMN_NAME = "test"

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
    }
}