package com.jayrave.falkon.sqlBuilders.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleInsertSqlBuilderTest {

    private val tableName = "test"

    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildThrowsForEmptyColumnsIterable() {
        SimpleInsertSqlBuilder.build("test", emptyList(), ARG_PLACEHOLDER)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildInsertOrReplaceThrowsForEmptyColumnsIterable() {
        SimpleInsertSqlBuilder.buildInsertOrReplace("I_O_R", "test", emptyList(), ARG_PLACEHOLDER)
    }


    @Test
    fun testSuccessfulBuild() {
        val actualSql = SimpleInsertSqlBuilder.build(
                tableName, listOf("column_name_1", "column_name_2"), ARG_PLACEHOLDER
        )

        val expectedSql = "INSERT INTO $tableName (column_name_1, column_name_2) VALUES (?, ?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testSuccessfulBuildInsertOrReplace() {
        val actualSql = SimpleInsertSqlBuilder.buildInsertOrReplace(
                "I_O_R", tableName, listOf("column_name_1"), ARG_PLACEHOLDER
        )

        val expectedSql = "I_O_R INTO $tableName (column_name_1) VALUES (?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"
    }
}