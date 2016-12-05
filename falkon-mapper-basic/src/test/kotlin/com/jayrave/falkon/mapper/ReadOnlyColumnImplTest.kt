package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.testLib.StaticDataProducer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class ReadOnlyColumnImplTest {

    @Test
    fun `compute property from non null type`() {
        val column: ReadOnlyColumn<UUID> = buildReadOnlyColumnImplForTest(nonNullUuidConverter())
        val inputString = UUID.randomUUID().toString()
        val dataProducer = StaticDataProducer.createForString(inputString)
        assertThat(column.computePropertyFrom(dataProducer)).isEqualTo(UUID.fromString(inputString))
    }

    @Test
    fun `compute property from nullable type with non null value`() {
        val column: ReadOnlyColumn<UUID?> = buildReadOnlyColumnImplForTest(nullableUuidConverter())
        val inputString = UUID.randomUUID().toString()
        val dataProducer = StaticDataProducer.createForString(inputString)
        assertThat(column.computePropertyFrom(dataProducer)).isEqualTo(UUID.fromString(inputString))
    }

    @Test
    fun `compute property from nullable type with null value`() {
        val column: ReadOnlyColumn<UUID?> = buildReadOnlyColumnImplForTest(nullableUuidConverter())
        val dataProducer = StaticDataProducer.createForString(null)
        assertThat(column.computePropertyFrom(dataProducer)).isNull()
    }


    companion object {

        private fun nonNullUuidConverter() = NullableToNonNullConverter(nullableUuidConverter())
        private fun nullableUuidConverter() = NullableUuidConverter()

        private fun <C> buildReadOnlyColumnImplForTest(converter: Converter<C>): ReadOnlyColumn<C> {
            return ReadOnlyColumnImpl("test", converter)
        }


        private class NullableUuidConverter : Converter<UUID?> {

            override val dbType: Type = Type.STRING

            override fun from(dataProducer: DataProducer): UUID? {
                return when (dataProducer.isNull()) {
                    true -> null
                    else -> UUID.fromString(dataProducer.getString())
                }
            }

            override fun to(value: UUID?, dataConsumer: DataConsumer) {
                dataConsumer.put(value?.toString())
            }
        }
    }
}