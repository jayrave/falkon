package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
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
    fun testWithSingleSelectedColumnWithoutAlias() {
        val actualSql = callBuildQuerySqlFromParts(
                columns = listOf(SelectColumnInfoForTest("column_name", null))
        )

        val expectedSql = "SELECT column_name FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithSingleSelectedColumnWithAlias() {
        val actualSql = callBuildQuerySqlFromParts(
                columns = listOf(SelectColumnInfoForTest("column_name", "aliased_column_name"))
        )

        val expectedSql = "SELECT column_name AS aliased_column_name FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithMultipleSelectedColumnsWithoutAliases() {
        val actualSql = callBuildQuerySqlFromParts(columns = listOf(
                SelectColumnInfoForTest("column_name_1", null),
                SelectColumnInfoForTest("column_name_2", null)
        ))

        val expectedSql = "SELECT column_name_1, column_name_2 FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithMultipleSelectedColumnsWithAliases() {
        val actualSql = callBuildQuerySqlFromParts(columns = listOf(
                SelectColumnInfoForTest("column_name_1", "aliased_column_name_1"),
                SelectColumnInfoForTest("column_name_2", "aliased_column_name_2")
        ))

        val expectedSql = "SELECT " +
                "column_name_1 AS aliased_column_name_1, " +
                "column_name_2 AS aliased_column_name_2 FROM $tableName"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithMultipleSelectedColumnsWithAndWithoutAliases() {
        val actualSql = callBuildQuerySqlFromParts(columns = listOf(
                SelectColumnInfoForTest("column_name_1", null),
                SelectColumnInfoForTest("column_name_2", "aliased_column_name_2")
        ))

        val expectedSql = "SELECT " +
                "column_name_1, column_name_2 AS aliased_column_name_2 " +
                "FROM $tableName"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithOneJoin() {
        val actualSql = callBuildQuerySqlFromParts(
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, "$tableName.column_name_1",
                        "table_2", "table_2.column_name_1"
                ))
        )

        val expectedSql = "SELECT * FROM $tableName " +
                "INNER JOIN table_2 ON $tableName.column_name_1 = table_2.column_name_1"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithMultiJoins() {
        val actualSql = callBuildQuerySqlFromParts(
                joinInfos = listOf(
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, "$tableName.column_name_1",
                                "table_2", "table_2.column_name_1"
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.LEFT_OUTER_JOIN, "$tableName.column_name_2",
                                "table_3", "table_3.column_name_1"
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.RIGHT_OUTER_JOIN, "$tableName.column_name_3",
                                "table_4", "table_4.column_name_1"
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.FULL_OUTER_JOIN, "$tableName.column_name_4",
                                "table_5", "table_5.column_name_1"
                        )
                )
        )

        val expectedSql = "SELECT * FROM $tableName " +
                "INNER JOIN table_2 ON $tableName.column_name_1 = table_2.column_name_1 " +
                "LEFT OUTER JOIN table_3 ON $tableName.column_name_2 = table_3.column_name_1 " +
                "RIGHT OUTER JOIN table_4 ON $tableName.column_name_3 = table_4.column_name_1 " +
                "FULL OUTER JOIN table_5 ON $tableName.column_name_4 = table_5.column_name_1"

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
    fun testWithEverythingWithoutAliases() {
        val actualSql = callBuildQuerySqlFromParts(
                distinct = true,
                columns = listOf(
                        SelectColumnInfoForTest("column_name_1", null),
                        SelectColumnInfoForTest("column_name_2", null)
                ),
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, "$tableName.column_name_1",
                        "table_2", "table_2.column_name_1"
                )),
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
                "INNER JOIN table_2 ON $tableName.column_name_1 = table_2.column_name_1 " +
                "WHERE column_name_3 = ? AND column_name_4 = ? " +
                "GROUP BY column_name_5 " +
                "ORDER BY column_name_6 DESC " +
                "LIMIT 5 OFFSET 6"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testWithEverythingWithAndWithoutAliases() {
        val actualSql = callBuildQuerySqlFromParts(
                tableName = tableName,
                distinct = true,
                columns = listOf(
                        SelectColumnInfoForTest("column_name_1", null),
                        SelectColumnInfoForTest("column_name_2", "cn2")
                ),
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, "column_name_1", "table_2", "column_name_2"
                )),
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
        val expectedSql = "SELECT DISTINCT column_name_1, column_name_2 AS cn2 " +
                "FROM $tableName " +
                "INNER JOIN table_2 ON column_name_1 = column_name_2 " +
                "WHERE column_name_3 = ? AND column_name_4 = ? " +
                "GROUP BY column_name_5 " +
                "ORDER BY column_name_6 DESC " +
                "LIMIT 5 OFFSET 6"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    private fun callBuildQuerySqlFromParts(
            tableName: String = this.tableName, distinct: Boolean = false,
            columns: Iterable<SelectColumnInfo>? = null, joinInfos: Iterable<JoinInfo>? = null,
            whereSections: Iterable<WhereSection>? = null, groupBy: Iterable<String>? = null,
            orderBy: Iterable<OrderInfo>? = null, limit: Long? = null, offset: Long? = null):
            String {

        return SimpleQuerySqlBuilder.build(
                tableName = tableName, distinct = distinct, columns = columns,
                joinInfos = joinInfos, whereSections = whereSections, groupBy = groupBy,
                orderBy = orderBy, limit = limit, offset = offset, argPlaceholder = "?"
        )
    }


    private class SelectColumnInfoForTest(
            override val columnName: String,
            override val alias: String?) : SelectColumnInfo


    private class JoinInfoForTest(
            override val type: JoinInfo.Type,
            override val qualifiedLocalColumnName: String,
            override val nameOfTableToJoin: String,
            override val qualifiedColumnNameFromTableToJoin: String) : JoinInfo


    private class OrderInfoForTest(
            override val columnName: String, override val ascending: Boolean) :
            OrderInfo
}