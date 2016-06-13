package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InsertKtTest {

    private val tableName = "test"

    @Test
    fun testBuildInsertSqlFromPartsReturnsNullForEmptyColumnsIterable() {
        val actualSql = buildInsertSqlFromParts("test", emptyList())
        assertThat(actualSql).isNull()
    }


    @Test
    fun testBuildInsertSqlFromParts() {
        val actualSql = buildInsertSqlFromParts(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3")
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "INSERT INTO $tableName " +
                "(column_name_1, column_name_2, column_name_3) " +
                "VALUES (?, ?, ?)"

        assertThat(actualSql).isEqualTo(expectedSql)
    }
}