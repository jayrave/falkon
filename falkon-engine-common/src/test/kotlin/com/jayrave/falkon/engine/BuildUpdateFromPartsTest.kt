package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.OneArgPredicate
import com.jayrave.falkon.exceptions.SQLException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BuildUpdateFromPartsTest {

    private val tableName = "test"

    @Test
    fun testBuildUpdateSqlOrNullReturnsNullForEmptyColumnsIterable() {
        val compiledStatement = SqlBuilderFromParts.buildUpdateSqlOrNull("test", emptyList(), null)
        assertThat(compiledStatement).isNull()
    }


    @Test(expected = SQLException::class)
    fun testBuildUpdateSqlOrThrowThrowsForEmptyColumnsIterable() {
        SqlBuilderFromParts.buildUpdateSqlOrThrow("test", emptyList(), null)
    }


    @Test
    fun testBuildUpdateSqlOrThrowsWithoutWhere() {
        val actualSql = SqlBuilderFromParts.buildUpdateSqlOrThrow(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"), null
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "UPDATE $tableName SET " +
                "column_name_1 = ?, column_name_2 = ?, column_name_3 = ?"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildUpdateSqlOrThrowsWithWhere() {
        val whereSections = listOf(
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                SimpleConnector(SimpleConnector.Type.AND),
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
        )

        val actualSql = SqlBuilderFromParts.buildUpdateSqlOrThrow(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"), whereSections
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "UPDATE $tableName " +
                "SET column_name_1 = ?, column_name_2 = ?, column_name_3 = ? " +
                "WHERE column_name_1 = ? AND column_name_2 = ?"

        assertThat(actualSql).isEqualTo(expectedSql)
    }
}