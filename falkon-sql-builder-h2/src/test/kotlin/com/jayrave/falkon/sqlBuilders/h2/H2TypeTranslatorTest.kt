package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.engine.Type
import org.assertj.core.api.Assertions.*

import org.junit.Test

class H2TypeTranslatorTest{

    private val typeTranslator = H2TypeTranslator()

    @Test
    fun testTranslateForShort() {
        val input = Type.SHORT
        assertThat(typeTranslator.translate(input)).isEqualTo("SMALLINT")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("SMALLINT(5)")
    }


    @Test
    fun testTranslateForInt() {
        val input = Type.INT
        assertThat(typeTranslator.translate(input)).isEqualTo("INTEGER")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("INTEGER(5)")
    }


    @Test
    fun testTranslateForLong() {
        val input = Type.LONG
        assertThat(typeTranslator.translate(input)).isEqualTo("BIGINT")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("BIGINT(5)")
    }


    @Test
    fun testTranslateForFloat() {
        val input = Type.FLOAT
        assertThat(typeTranslator.translate(input)).isEqualTo("REAL")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("REAL(5)")
    }


    @Test
    fun testTranslateForDouble() {
        val input = Type.DOUBLE
        assertThat(typeTranslator.translate(input)).isEqualTo("DOUBLE")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("DOUBLE(5)")
    }


    @Test
    fun testTranslateForString() {
        val input = Type.STRING
        assertThat(typeTranslator.translate(input)).isEqualTo("VARCHAR")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("VARCHAR(5)")
    }


    @Test
    fun testTranslateForBlob() {
        val input = Type.BLOB
        assertThat(typeTranslator.translate(input)).isEqualTo("BLOB")
        assertThat(typeTranslator.translate(input, 5)).isEqualTo("BLOB(5)")
    }
}