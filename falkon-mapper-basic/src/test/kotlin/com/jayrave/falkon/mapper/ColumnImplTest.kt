package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.testLib.StaticDataProducer
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class ColumnImplTest {

    @Test
    fun testColumnImplReturnsTheCorrectDbType() {
        val uuidColumn: Column<Any, UUID> = buildColumnImplForTest(nonNullUuidConverter())
        val longColumn: Column<Any, Long> = buildColumnImplForTest(
                NullableToNonNullConverter(NullableLongConverter())
        )

        assertThat(uuidColumn.dbType).isEqualTo(Type.STRING)
        assertThat(longColumn.dbType).isEqualTo(Type.LONG)
    }

    @Test
    fun testComputeStorageFormOfForNonNullType() {
        val column: Column<Any, UUID> = buildColumnImplForTest(nonNullUuidConverter())
        val inputId = UUID.randomUUID()
        assertThat(column.computeStorageFormOf(inputId)).isEqualTo(inputId.toString())
    }

    @Test
    fun testComputeStorageFormOfForNullableType() {
        val column: Column<Any, UUID?> = buildColumnImplForTest(nullableUuidConverter())
        val inputId = UUID.randomUUID()
        assertThat(column.computeStorageFormOf(inputId)).isEqualTo(inputId.toString())
    }

    @Test
    fun testPutStorageFormInForNonNullType() {
        val column: Column<Any, UUID> = buildColumnImplForTest(nonNullUuidConverter())
        val inputId = UUID.randomUUID()
        val dataConsumer = ValueHoldingDataConsumer()
        column.putStorageFormIn(inputId, dataConsumer)
        assertThat(dataConsumer.mostRecentConsumedValue).isEqualTo(inputId.toString())
    }

    @Test
    fun testPutStorageFormInForNullableType() {
        val column: Column<Any, UUID?> = buildColumnImplForTest(nullableUuidConverter())
        val inputId = UUID.randomUUID()
        val dataConsumer = ValueHoldingDataConsumer()
        column.putStorageFormIn(inputId, dataConsumer)
        assertThat(dataConsumer.mostRecentConsumedValue).isEqualTo(inputId.toString())
    }

    @Test
    fun testComputePropertyFromForNonNullType() {
        val column: Column<Any, UUID> = buildColumnImplForTest(nonNullUuidConverter())
        val inputString = UUID.randomUUID().toString()
        val dataProducer = StaticDataProducer.createForString(inputString)
        assertThat(column.computePropertyFrom(dataProducer)).isEqualTo(UUID.fromString(inputString))
    }

    @Test
    fun testComputePropertyFromForNullableType() {
        val column: Column<Any, UUID?> = buildColumnImplForTest(nullableUuidConverter())
        val inputString = UUID.randomUUID().toString()
        val dataProducer = StaticDataProducer.createForString(inputString)
        assertThat(column.computePropertyFrom(dataProducer)).isEqualTo(UUID.fromString(inputString))
    }


    companion object {

        private fun nonNullUuidConverter() = NullableToNonNullConverter(nullableUuidConverter())
        private fun nullableUuidConverter() = NullableUuidConverter()

        private fun <C> buildColumnImplForTest(converter: Converter<C>): Column<Any, C> {
            return ColumnImpl(mock(), "test", false, mock(), converter)
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