package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TableConfigurationExtnTest {

    @Test
    fun testRegisterDefaultConverters() {
        val configuration = TableConfigurationImpl(mock<Engine>(), mock(), mock())
        configuration.registerDefaultConverters()

        // Check primitives
        assertThat(configuration.getConverterForNonNullValuesOf(
                Byte::class.javaPrimitiveType!!
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Char::class.javaPrimitiveType!!
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Short::class.javaPrimitiveType!!
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Int::class.javaPrimitiveType!!
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Long::class.javaPrimitiveType!!
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Float::class.javaPrimitiveType!!
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Double::class.javaPrimitiveType!!
        )).isNotNull()

        // Check non-null form of boxed primitives
        assertThat(configuration.getConverterForNonNullValuesOf(
                Byte::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Char::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Short::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Int::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Long::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Float::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                Double::class.javaObjectType
        )).isNotNull()

        // Check nullable form of boxed primitives
        assertThat(configuration.getConverterForNullableValuesOf(
                Byte::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                Char::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                Short::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                Int::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                Long::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                Float::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                Double::class.javaObjectType
        )).isNotNull()

        // Check the following => String, String?, ByteArray, ByteArray?
        assertThat(configuration.getConverterForNonNullValuesOf(
                String::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                String::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNonNullValuesOf(
                ByteArray::class.javaObjectType
        )).isNotNull()

        assertThat(configuration.getConverterForNullableValuesOf(
                ByteArray::class.javaObjectType
        )).isNotNull()
    }
}