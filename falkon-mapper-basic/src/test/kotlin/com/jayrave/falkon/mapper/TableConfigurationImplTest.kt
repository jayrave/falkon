package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class TableConfigurationImplTest {

    val configuration = TableConfigurationImpl(mock<Engine>(), mock(), mock())

    @Test
    fun testGetConverterForNullableTypeReturnsNull() {
        assertThat(configuration.getConverterForNullableType(UUID::class.java)).isNull()
    }


    @Test
    fun testGetConverterForNonNullTypeReturnsNull() {
        assertThat(configuration.getConverterForNonNullType(UUID::class.java)).isNull()
    }


    @Test
    fun testConverterRegistrationForNullableType() {
        val clazz = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableType(clazz, converter, false)
        assertThat(configuration.getConverterForNullableType(clazz)).isSameAs(converter)
    }


    @Test
    fun testConverterRegistrationForNonNullType() {
        val clazz = String::class.java
        val converter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullType(clazz, converter)
        assertThat(configuration.getConverterForNonNullType(clazz)).isSameAs(converter)
    }


    @Test
    fun testConverterRegistrationForNullableTypeOverWritingExistingConverter() {
        val clazz = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableType(clazz, converter, false)
        assertThat(configuration.getConverterForNullableType(clazz)).isSameAs(converter)

        val overWritingConverter = NullableStringConverter()
        configuration.registerForNullableType(clazz, overWritingConverter, false)
        assertThat(configuration.getConverterForNullableType(clazz)).isSameAs(overWritingConverter)
    }


    @Test
    fun testConverterRegistrationForNonNullTypeOverWritingExistingConverter() {
        val clazz = String::class.java
        val converter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullType(clazz, converter)
        assertThat(configuration.getConverterForNonNullType(clazz)).isSameAs(converter)

        val overWritingConverter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullType(clazz, overWritingConverter)
        assertThat(configuration.getConverterForNonNullType(clazz)).isSameAs(overWritingConverter)
    }


    @Test
    fun testAutoWrappingNullableTypeConverterForNonNullType() {
        val clazz = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableType(clazz, converter, true)
        assertThat(configuration.getConverterForNullableType(clazz)).isSameAs(converter)
        assertThat(configuration.getConverterForNonNullType(clazz)).isNotNull()
    }


    @Test
    fun testAutoWrappingNullableTypeConverterDoesNotOverWriteExistingNonNullType() {
        val clazz = String::class.java
        val nullableConverter = NullableStringConverter()
        val nonNullConverter = NullableToNonNullConverter(nullableConverter)
        configuration.registerForNonNullType(clazz, nonNullConverter)
        configuration.registerForNullableType(clazz, nullableConverter, true)
        assertThat(configuration.getConverterForNullableType(clazz)).isSameAs(nullableConverter)
        assertThat(configuration.getConverterForNonNullType(clazz)).isSameAs(nonNullConverter) // No over writes
    }
}