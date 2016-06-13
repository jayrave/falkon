package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UpdateKtTest {

    private val tableName = "test"

    @Test
    fun testBuildUpdateSqlFromPartsReturnsNullForEmptyColumnsIterable() {
        val compiledStatement = buildUpdateSqlFromParts("test", emptyList(), null)
        assertThat(compiledStatement).isNull()
    }


    @Test
    fun testBuildUpdateSqlFromPartsWithoutWhere() {
        val actualSql = buildUpdateSqlFromParts(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"), null
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "UPDATE $tableName SET " +
                "column_name_1 = ?, column_name_2 = ?, column_name_3 = ?"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildUpdateSqlFromPartsWithWhere() {
        val whereSections = listOf(
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                SimpleConnector(SimpleConnector.Type.AND),
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
        )

        val actualSql = buildUpdateSqlFromParts(
                tableName, listOf("column_name_1", "column_name_2", "column_name_3"), whereSections
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "UPDATE $tableName " +
                "SET column_name_1 = ?, column_name_2 = ?, column_name_3 = ? " +
                "WHERE column_name_1 = ? AND column_name_2 = ?"

        assertThat(actualSql).isEqualTo(expectedSql)
    }
}