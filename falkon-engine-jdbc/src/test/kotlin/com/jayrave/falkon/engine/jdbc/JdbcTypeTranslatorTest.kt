package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.Type
import org.assertj.core.api.Assertions.*

import org.junit.Test

class JdbcTypeTranslatorTest {

    private val typeTranslator = JdbcTypeTranslator()

    @Test
    fun testTranslateForShort() {
        val input = Type.SHORT
        val expectedOutput = "SMALLINT"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForInt() {
        val input = Type.INT
        val expectedOutput = "INTEGER"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForLong() {
        val input = Type.LONG
        val expectedOutput = "BIGINT"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForFloat() {
        val input = Type.FLOAT
        val expectedOutput = "REAL"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForDouble() {
        val input = Type.DOUBLE
        val expectedOutput = "DOUBLE"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForString() {
        val input = Type.STRING
        val expectedOutput = "VARCHAR"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForBlob() {
        val input = Type.BLOB
        val expectedOutput = "BLOB"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }
}