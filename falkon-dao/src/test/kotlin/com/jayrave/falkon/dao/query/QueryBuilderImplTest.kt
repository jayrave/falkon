package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.query.testLib.*
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * As of July 25, 2016, [com.jayrave.falkon.dao.query.lenient.QueryBuilderImpl] is being used
 * by [QueryBuilderImpl] under the hood. Therefore, this test class has only a few smoke tests
 * that touches almost all the functionality of [QueryBuilderImpl]
 */
class QueryBuilderImplTest {

    @Test
    fun testQueryingWithoutSettingAnything() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(table, querySqlBuilder, ARG_PLACEHOLDER)
        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = querySqlBuilder,
                columns = buildColumnInfoList(table)
        )
        val expectedQuery = QueryImpl(expectedSql, emptyList())

        // Verify
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).isEmpty()
    }


    @Test
    fun testQueryWithAllOptions() {
        val bundle = Bundle.default()
        val table = bundle.table
        val tableForJoin = TableForTest("table_for_join")
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(table, querySqlBuilder, ARG_PLACEHOLDER)
        builder
                .distinct()
                .select(table.int)
                .join(table.long, tableForJoin.blob)
                .where().eq(table.double, 5.0)
                .groupBy(table.blob)
                .orderBy(table.nullableFloat, true)
                .limit(5)
                .offset(8)

        // build & compile
        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val primaryTableName = table.name
        val expectedSql = buildQuerySql(
                tableName = primaryTableName,
                querySqlBuilder = bundle.querySqlBuilder,
                distinct = true,
                columns = listOf(table.int.buildSelectColumnInfoForTest()),
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, table.long.qualifiedName,
                        tableForJoin.name, tableForJoin.blob.qualifiedName
                )),
                whereSections = listOf(OneArgPredicate(
                        OneArgPredicate.Type.EQ, table.double.qualifiedName
                )),
                groupBy = listOf(table.blob.qualifiedName),
                orderBy = listOf(OrderInfoForTest(table.nullableFloat.qualifiedName, true)),
                limit = 5, offset = 8
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5.0))

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name, tableForJoin.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.doubleBoundAt(1)).isEqualTo(5.0)
    }
}