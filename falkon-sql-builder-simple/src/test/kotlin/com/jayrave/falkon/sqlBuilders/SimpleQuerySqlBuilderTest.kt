package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SimpleQuerySqlBuilderTest {

    private val tableName = "test"

    @Test
    fun testWithoutAnything() {
        val actualSql = callBuildQuerySqlFromParts()
        val expectedSql = "SELECT * FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithDistinct() {
        val actualSql = callBuildQuerySqlFromParts(distinct = true)
        val expectedSql = "SELECT DISTINCT * FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithSingleSelectedColumn() {
        val actualSql = callBuildQuerySqlFromParts(columns = listOf("column_name"))
        val expectedSql = "SELECT column_name FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        val actualSql = callBuildQuerySqlFromParts(
                columns = listOf("column_name_1", "column_name_2")
        )

        val expectedSql = "SELECT column_name_1, column_name_2 FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithWhere() {
        val actualSql = callBuildQuerySqlFromParts(
                whereSections = listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
                )
        )

        val expectedSql = "SELECT * FROM $tableName WHERE column_name_1 = ? AND column_name_2 = ?"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testGroupByWithOneColumn() {
        val actualSql = callBuildQuerySqlFromParts(groupBy = listOf("column_name"))
        val expectedSql = "SELECT * FROM $tableName GROUP BY column_name"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        val actualSql = callBuildQuerySqlFromParts(
                groupBy = listOf("column_name_1", "column_name_2")
        )

        val expectedSql = "SELECT * FROM $tableName GROUP BY column_name_1, column_name_2"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testOrderByWithOneColumn() {
        val actualSql = callBuildQuerySqlFromParts(
                orderBy = listOf(OrderInfoForTest("column_name", true))
        )

        val expectedSql = "SELECT * FROM $tableName ORDER BY column_name ASC"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        val actualSql = callBuildQuerySqlFromParts(
                orderBy = listOf(
                        OrderInfoForTest("column_name_1", true),
                        OrderInfoForTest("column_name_2", false)
                )
        )

        val expectedSql = "SELECT * FROM $tableName ORDER BY column_name_1 ASC, column_name_2 DESC"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testLimit() {
        val actualSql = callBuildQuerySqlFromParts(limit = 5)
        val expectedSql = "SELECT * FROM $tableName LIMIT 5"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testOffset() {
        val actualSql = callBuildQuerySqlFromParts(offset = 5)
        val expectedSql = "SELECT * FROM $tableName OFFSET 5"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithEverything() {
        val actualSql = callBuildQuerySqlFromParts(
                distinct = true, columns = listOf("column_name_1", "column_name_2"),
                whereSections = listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_3"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_4")
                ),
                groupBy = listOf("column_name_5"),
                orderBy = listOf(OrderInfoForTest("column_name_6", false)),
                limit = 5, offset = 6
        )

        @Suppress("ConvertToStringTemplate")
        val expectedSql = "SELECT DISTINCT column_name_1, column_name_2 FROM $tableName " +
                "WHERE column_name_3 = ? AND column_name_4 = ? " +
                "GROUP BY column_name_5 " +
                "ORDER BY column_name_6 DESC " +
                "LIMIT 5 OFFSET 6"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    private fun callBuildQuerySqlFromParts(
            tableName: String = this.tableName, distinct: Boolean = false,
            columns: Iterable<String>? = null, whereSections: Iterable<WhereSection>? = null,
            groupBy: Iterable<String>? = null, orderBy: Iterable<OrderInfo>? = null,
            limit: Long? = null, offset: Long? = null): String {

        return SimpleQuerySqlBuilder().build(
                tableName = tableName, distinct = distinct, columns = columns,
                whereSections = whereSections, groupBy = groupBy, orderBy = orderBy,
                limit = limit, offset = offset, argPlaceholder = "?",
                orderByAscendingKey = "ASC", orderByDescendingKey = "DESC"
        )
    }


    private class OrderInfoForTest(
            override val columnName: String, override val ascending: Boolean) :
            OrderInfo
}