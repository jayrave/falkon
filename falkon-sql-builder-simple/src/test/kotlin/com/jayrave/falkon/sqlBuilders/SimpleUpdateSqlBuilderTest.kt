package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleUpdateSqlBuilderTest {

    private val tableName = "test"

    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildThrowsForEmptyColumnsIterable() {
        SimpleUpdateSqlBuilder().build("test", emptyList(), null, ARG_PLACEHOLDER)
    }


    @Test
    fun testSuccessfulBuildWithoutWhere() {
        val actualSql = SimpleUpdateSqlBuilder().build(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"),
                null, ARG_PLACEHOLDER
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "UPDATE $tableName SET " +
                "column_name_1 = ?, column_name_2 = ?, column_name_3 = ?"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testSuccessfulBuildWithWhere() {
        val whereSections = listOf(
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                SimpleConnector(SimpleConnector.Type.AND),
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
        )

        val actualSql = SimpleUpdateSqlBuilder().build(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"),
                whereSections, ARG_PLACEHOLDER
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "UPDATE $tableName " +
                "SET column_name_1 = ?, column_name_2 = ?, column_name_3 = ? " +
                "WHERE column_name_1 = ? AND column_name_2 = ?"

        assertThat(actualSql).isEqualTo(expectedSql)
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"
    }
}