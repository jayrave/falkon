package com.jayrave.falkon.engine

import com.jayrave.falkon.exceptions.SQLException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BuildInsertFromPartsTest {

    private val tableName = "test"

    @Test
    fun testBuildInsertSqlOrNullReturnsNullForEmptyColumnsIterable() {
        val actualSql = SqlBuilderFromParts.buildInsertSqlOrNull("test", emptyList())
        assertThat(actualSql).isNull()
    }


    @Test(expected = SQLException::class)
    fun testBuildInsertSqlOrThrowThrowsForEmptyColumnsIterable() {
        SqlBuilderFromParts.buildInsertSqlOrThrow("test", emptyList())
    }


    @Test
    fun testBuildInsertSqlOrThrows() {
        val actualSql = SqlBuilderFromParts.buildInsertSqlOrThrow(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3")
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "INSERT INTO $tableName " +
                "(column_name_1, column_name_2, column_name_3) " +
                "VALUES (?, ?, ?)"

        assertThat(actualSql).isEqualTo(expectedSql)
    }
}