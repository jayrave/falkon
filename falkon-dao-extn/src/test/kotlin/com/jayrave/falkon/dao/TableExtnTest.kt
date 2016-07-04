package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class TableExtnTest {

    @Test
    fun testExtractIdFrom() {
        val inputId = UUID.randomUUID()
        val model = ModelForTest(id = inputId)
        val actualValue = TableForTest().extractIdFrom(model)
        assertThat(actualValue).isEqualTo(inputId)
    }
}