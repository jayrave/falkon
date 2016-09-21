package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.dao.testLib.TableForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ColumnExtnTest {

    @Test
    fun testQualifiedNameProperty() {
        val table = TableForTest("table_for_test")
        assertThat(table.long.qualifiedName).isEqualTo("table_for_test.long")
    }


    @Test
    fun testUniqueNameInDbProperty() {
        val table = TableForTest("table_for_test")
        assertThat(table.long.uniqueNameInDb).isEqualTo("table_for_test_long")
    }
}