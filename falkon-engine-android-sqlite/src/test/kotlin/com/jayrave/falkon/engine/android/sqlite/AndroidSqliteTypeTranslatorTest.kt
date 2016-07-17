package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.Type
import org.assertj.core.api.Assertions.*
import org.junit.Test

class AndroidSqliteTypeTranslatorTest {

    private val typeTranslator = AndroidSqliteTypeTranslator()

    @Test
    fun testTranslateForShort() {
        val input = Type.SHORT
        val expectedOutput = "INTEGER"
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
        val expectedOutput = "INTEGER"
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
        val expectedOutput = "REAL"
        assertThat(typeTranslator.translate(input)).isEqualTo(expectedOutput)
        assertThat(typeTranslator.translate(input, 5)).isEqualTo(expectedOutput)
    }


    @Test
    fun testTranslateForString() {
        val input = Type.STRING
        val expectedOutput = "TEXT"
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