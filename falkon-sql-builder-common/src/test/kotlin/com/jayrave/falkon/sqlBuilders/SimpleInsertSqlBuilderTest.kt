package com.jayrave.falkon.sqlBuilders

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleInsertSqlBuilderTest {

    private val tableName = "test"

    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildThrowsForEmptyColumnsIterable() {
        SimpleInsertSqlBuilder().build("test", emptyList(), ARG_PLACEHOLDER)
    }


    @Test
    fun testSuccessfulBuild() {
        val actualSql = SimpleInsertSqlBuilder().build(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"),
                ARG_PLACEHOLDER
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "INSERT INTO $tableName " +
                "(column_name_1, column_name_2, column_name_3) " +
                "VALUES (?, ?, ?)"

        assertThat(actualSql).isEqualTo(expectedSql)
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"
    }
}