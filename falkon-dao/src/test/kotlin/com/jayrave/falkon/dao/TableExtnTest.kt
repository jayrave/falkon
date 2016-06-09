package com.jayrave.falkon.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TableExtnTest {

    @Test
    fun testExtractIdFrom() {
        val model = ModelForTest(id = 42)
        val actualValue = TableForTest().extractIdFrom(model)
        assertThat(actualValue).isEqualTo(42)
    }
}