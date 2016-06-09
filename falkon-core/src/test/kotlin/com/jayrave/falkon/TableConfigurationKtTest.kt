package com.jayrave.falkon

import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TableConfigurationKtTest {

    @Test
    fun testRegisterDefaultConverters() {
        val configuration = TableConfigurationImpl(mock<Engine>(), mock())
        configuration.registerDefaultConverters()

        // Check primitives
        assertThat(configuration.getConverterForNonNullType(Byte::class.javaPrimitiveType!!))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Char::class.javaPrimitiveType!!))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Short::class.javaPrimitiveType!!))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Int::class.javaPrimitiveType!!))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Long::class.javaPrimitiveType!!))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Float::class.javaPrimitiveType!!))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Double::class.javaPrimitiveType!!))
                .isNotNull()

        // Check non-null form of boxed primitives
        assertThat(configuration.getConverterForNonNullType(Byte::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Char::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Short::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Int::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Long::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Float::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(Double::class.javaObjectType))
                .isNotNull()

        // Check nullable form of boxed primitives
        assertThat(configuration.getConverterForNullableType(Byte::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(Char::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(Short::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(Int::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(Long::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(Float::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(Double::class.javaObjectType))
                .isNotNull()

        // Check the following => String, String?, ByteArray, ByteArray?
        assertThat(configuration.getConverterForNonNullType(String::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(String::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNonNullType(ByteArray::class.javaObjectType))
                .isNotNull()

        assertThat(configuration.getConverterForNullableType(ByteArray::class.javaObjectType))
                .isNotNull()
    }
}