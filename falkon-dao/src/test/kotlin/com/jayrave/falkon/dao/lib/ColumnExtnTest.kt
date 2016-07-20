package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.dao.testLib.TableForTest
import org.assertj.core.api.Assertions.*
import org.junit.Test

class ColumnExtnTest {

    @Test
    fun testQualifiedNameProperty() {
        val table = TableForTest("table_for_test")
        assertThat(table.long.qualifiedName).isEqualTo("table_for_test.long")
    }


    @Test
    fun testGetAppropriateNameWithQualifiedName() {
        val table = TableForTest("table_for_test")
        assertThat(table.long.getAppropriateName(true)).isEqualTo("table_for_test.long")
    }


    @Test
    fun testGetAppropriateNameWithoutQualifiedName() {
        val table = TableForTest("table_for_test")
        assertThat(table.long.getAppropriateName(false)).isEqualTo("long")
    }
}