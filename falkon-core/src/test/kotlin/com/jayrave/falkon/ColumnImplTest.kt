package com.jayrave.falkon

import com.jayrave.falkon.lib.StaticDataProducer
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class ColumnImplTest {

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

    @Test
    fun testNullToSqlSubstituteWithNonNullSubstitute() {
        val inputId = UUID.randomUUID()
        val nullSubstitute = buildNullSubstitute(inputId)
        val column: Column<Any, UUID?> = buildColumnImplForTest(
                nullableUuidConverter(), nullToSqlSubstitute = nullSubstitute
        )

        assertThat(column.computeStorageFormOf(null)).isEqualTo(inputId.toString())
    }

    @Test
    fun testNullToSqlSubstituteWithNullSubstitute() {
        val column: Column<Any, UUID?> = buildColumnImplForTest(
                nullableUuidConverter(), nullToSqlSubstitute = buildNullSubstitute<UUID?>(null)
        )

        assertThat(column.computeStorageFormOf(null)).isNull()
    }

    @Test
    fun testNullFromSqlSubstituteWithNonNullSubstitute() {
        val inputId = UUID.randomUUID()
        val nullSubstitute = buildNullSubstitute(inputId)
        val column: Column<Any, UUID?> = buildColumnImplForTest(
                nullableUuidConverter(), nullFromSqlSubstitute = nullSubstitute
        )

        val dataProducer = StaticDataProducer.createForString(null)
        assertThat(column.computePropertyFrom(dataProducer)).isEqualTo(inputId)
    }

    @Test
    fun testNullFromSqlSubstituteWithNullSubstitute() {
        val column: Column<Any, UUID?> = buildColumnImplForTest(
                nullableUuidConverter(), nullFromSqlSubstitute = buildNullSubstitute<UUID?>(null)
        )

        val dataProducer = StaticDataProducer.createForString(null)
        assertThat(column.computePropertyFrom(dataProducer)).isNull()
    }


    companion object {

        private fun nonNullUuidConverter() = NullableToNonNullConverter(nullableUuidConverter())
        private fun nullableUuidConverter() = NullableUuidConverter()
        private fun <C> buildNullSubstitute(value: C) = object : NullSubstitute<C> { override fun value() = value }
        private fun <C> throwingNullSubstitute() = object : NullSubstitute<C> {
            override fun value() = throw RuntimeException()
        }

        private fun <C> buildColumnImplForTest(
                converter: Converter<C>, nullFromSqlSubstitute: NullSubstitute<C> = throwingNullSubstitute(),
                nullToSqlSubstitute: NullSubstitute<C> = throwingNullSubstitute()): Column<Any, C> {

            return ColumnImpl("test", mock(), converter, nullFromSqlSubstitute, nullToSqlSubstitute)
        }


        private class NullableUuidConverter : Converter<UUID?> {
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