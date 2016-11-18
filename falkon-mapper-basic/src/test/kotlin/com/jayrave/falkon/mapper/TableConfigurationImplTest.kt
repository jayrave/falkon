package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.reflect.Type
import java.util.*

class TableConfigurationImplTest {

    val configuration = TableConfigurationImpl(mock<Engine>(), mock(), mock())

    @Test
    fun testGetNullableValueConverterForUnregisteredClassReturnsNull() {
        assertThat(configuration.getConverterForNullableValuesOf(UUID::class.java)).isNull()
    }


    @Test
    fun testGetNullableValueConverterForUnregisteredTypeReturnsNull() {
        assertThat(configuration.getConverterForNullableValuesOf<UUID>(
                UUID::class.java as Type
        )).isNull()
    }


    @Test
    fun testGetNonNullValueConverterForUnregisteredClassReturnsNull() {
        assertThat(configuration.getConverterForNonNullValuesOf(UUID::class.java)).isNull()
    }


    @Test
    fun testGetNonNullValueConverterForUnregisteredTypeReturnsNull() {
        assertThat(configuration.getConverterForNonNullValuesOf<UUID>(
                UUID::class.java as Type
        )).isNull()
    }


    @Test
    fun testRegistrationOfNullableValueConverterForClass() {
        val clazz = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableValues(clazz, converter, false)
        assertThat(configuration.getConverterForNullableValuesOf(clazz)).isSameAs(converter)
    }


    @Test
    fun testRegistrationOfNullableValueConverterForType() {
        val type: Type = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableValues(type, converter, false)
        assertThat(configuration.getConverterForNullableValuesOf<String>(type)).isSameAs(converter)
    }


    @Test
    fun testRegistrationOfNonNullValueConverterForClass() {
        val clazz = String::class.java
        val converter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullValues(clazz, converter)
        assertThat(configuration.getConverterForNonNullValuesOf(clazz)).isSameAs(converter)
    }


    @Test
    fun testRegistrationOfNonNullValueConverterForType() {
        val type: Type = String::class.java
        val converter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullValues(type, converter)
        assertThat(configuration.getConverterForNonNullValuesOf<String>(type)).isSameAs(converter)
    }


    @Test
    fun testNullableValueConverterRegistrationForClassOverWritesExisting() {
        val clazz = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableValues(clazz, converter, false)
        assertThat(configuration.getConverterForNullableValuesOf(clazz)).isSameAs(converter)

        val overWritingConverter = NullableStringConverter()
        configuration.registerForNullableValues(clazz, overWritingConverter, false)
        assertThat(configuration.getConverterForNullableValuesOf(clazz)).isSameAs(
                overWritingConverter
        )
    }


    @Test
    fun testNullableValueConverterRegistrationForTypeOverWritesExisting() {
        val type: Type = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableValues(type, converter, false)
        assertThat(configuration.getConverterForNullableValuesOf<String>(type)).isSameAs(converter)

        val overWritingConverter = NullableStringConverter()
        configuration.registerForNullableValues(type, overWritingConverter, false)
        assertThat(configuration.getConverterForNullableValuesOf<String>(type)).isSameAs(
                overWritingConverter
        )
    }


    @Test
    fun testNonNullValueConverterRegistrationForClassOverWritesExisting() {
        val clazz = String::class.java
        val converter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullValues(clazz, converter)
        assertThat(configuration.getConverterForNonNullValuesOf(clazz)).isSameAs(converter)

        val overWritingConverter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullValues(clazz, overWritingConverter)
        assertThat(configuration.getConverterForNonNullValuesOf(clazz)).isSameAs(
                overWritingConverter
        )
    }


    @Test
    fun testNonNullValueConverterRegistrationForTypeOverWritesExisting() {
        val type: Type = String::class.java
        val converter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullValues(type, converter)
        assertThat(configuration.getConverterForNonNullValuesOf<String>(type)).isSameAs(converter)

        val overWritingConverter = NullableToNonNullConverter(NullableStringConverter())
        configuration.registerForNonNullValues(type, overWritingConverter)
        assertThat(configuration.getConverterForNonNullValuesOf<String>(type)).isSameAs(
                overWritingConverter
        )
    }


    @Test
    fun testAutoWrappingNullableValueConverterForClass() {
        val clazz = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableValues(clazz, converter, true)
        assertThat(configuration.getConverterForNullableValuesOf(clazz)).isSameAs(converter)
        assertThat(configuration.getConverterForNonNullValuesOf(clazz)).isNotNull()
    }


    @Test
    fun testAutoWrappingNullableValueConverterForType() {
        val type: Type = String::class.java
        val converter = NullableStringConverter()
        configuration.registerForNullableValues(type, converter, true)
        assertThat(configuration.getConverterForNullableValuesOf<String>(type)).isSameAs(converter)
        assertThat(configuration.getConverterForNonNullValuesOf<String>(type)).isNotNull()
    }


    @Test
    fun testAutoWrappingNullableValueConverterDoesNotOverWriteExistingForClass() {
        val clazz = String::class.java
        val nullableConverter = NullableStringConverter()
        val nonNullConverter = NullableToNonNullConverter(nullableConverter)
        configuration.registerForNonNullValues(clazz, nonNullConverter)
        configuration.registerForNullableValues(clazz, nullableConverter, true)
        assertThat(configuration.getConverterForNullableValuesOf(clazz)).isSameAs(nullableConverter)
        assertThat(configuration.getConverterForNonNullValuesOf(clazz)).isSameAs(nonNullConverter)
    }


    @Test
    fun testAutoWrappingNullableValueConverterDoesNotOverWriteExistingForType() {
        val type: Type = String::class.java
        val nullableConverter = NullableStringConverter()
        val nonNullConverter = NullableToNonNullConverter(nullableConverter)
        configuration.registerForNonNullValues(type, nonNullConverter)
        configuration.registerForNullableValues(type, nullableConverter, true)
        assertThat(configuration.getConverterForNullableValuesOf<String>(type)).isSameAs(
                nullableConverter
        )

        assertThat(configuration.getConverterForNonNullValuesOf<String>(type)).isSameAs(
                nonNullConverter
        )
    }
}