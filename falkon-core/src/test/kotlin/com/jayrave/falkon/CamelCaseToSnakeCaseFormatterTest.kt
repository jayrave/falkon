package com.jayrave.falkon

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CamelCaseToSnakeCaseFormatterTest {

    val formatter: NameFormatter = CamelCaseToSnakeCaseFormatter()

    @Test
    fun testNoOp() {
        assertThat(formatter.format("test")).isEqualTo("test")
    }

    @Test
    fun testWithOneConnector() {
        assertThat(formatter.format("testTest")).isEqualTo("test_test")
    }

    @Test
    fun testWithMultipleConnectors() {
        assertThat(formatter.format("testTestTestTest")).isEqualTo("test_test_test_test")
    }

    @Test
    fun testWithSingleDigit() {
        assertThat(formatter.format("test1")).isEqualTo("test_1")
    }

    @Test
    fun testWithMultipleDigits() {
        assertThat(formatter.format("test123")).isEqualTo("test_123")
    }

    @Test
    fun testWithMixedDigitsAndTest() {
        assertThat(formatter.format("test123Test4Test56Test789")).isEqualTo(
                "test_123_test_4_test_56_test_789"
        )
    }
}