package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.query.testLib.QuerySqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledQueryForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QueryBuilderImplTest {

    @Test
    fun testQueryingWithoutSettingAnything() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        )

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, distinct = false
        )
    }


    @Test
    fun testWithDistinct() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).distinct()

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, distinct = true
        )
    }


    @Test
    fun testWithSingleSelectedColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).select(table.int)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder, querySqlBuilder = querySqlBuilder,
                columns = listOf("int")
        )
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).select(table.int, table.string)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder, querySqlBuilder = querySqlBuilder,
                columns = listOf("int", "string")
        )
    }


    @Test
    fun testRedefiningSelectedColumnsOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).select(table.int).select(table.int)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder, querySqlBuilder = querySqlBuilder,
                columns = listOf("int")
        )
    }


    @Test
    fun testWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        // build & compile
        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).where().eq(table.int, 5)

        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = querySqlBuilder,
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int"))
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5))

        // Verify
        assertEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testGroupByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).groupBy(table.int)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, groupBy = listOf("int")
        )
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).groupBy(table.string, table.blob)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, groupBy = listOf("string", "blob")
        )
    }


    @Test
    fun testRedefiningGroupByOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).groupBy(table.int).groupBy(table.string)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, groupBy = listOf("string")
        )
    }


    @Test
    fun testOrderByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).orderBy(table.int, true)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder, querySqlBuilder = querySqlBuilder,
                orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).orderBy(table.int, true).orderBy(table.blob, false)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder, querySqlBuilder = querySqlBuilder,
                orderBy = listOf(OrderInfoForTest("int", true), OrderInfoForTest("blob", false))
        )
    }


    @Test
    fun testSubsequentOrderByForTheSameColumnIsNoOp() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).orderBy(table.int, true).orderBy(table.int, false)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder, querySqlBuilder = querySqlBuilder,
                orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testLimit() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).limit(50)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, limit = 50
        )
    }


    @Test
    fun testOffset() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        ).offset(72)

        assertArgFreeStatement(
                table, engine, adderOrEnder = builder,
                querySqlBuilder = querySqlBuilder, offset = 72
        )
    }


    @Test
    fun testComplexQueryWithWhereAtLast() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        )
                .distinct()
                .select(table.string)
                .groupBy(table.blob)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)
                .where().eq(table.double, 5.0)

        verifyComplexWhere(table, engine, builder, querySqlBuilder)
    }


    @Test
    fun testComplexQueryWithWhereAtFirst() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        )
                .where().eq(table.double, 5.0)
                .distinct()
                .select(table.string)
                .groupBy(table.blob)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)

        verifyComplexWhere(table, engine, builder, querySqlBuilder)
    }


    @Test
    fun testComplexQueryWithCrazyOrdering() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        )
                .limit(5)
                .orderBy(table.int, true)
                .where().eq(table.double, 5.0)
                .offset(8)
                .groupBy(table.blob)
                .select(table.string)
                .distinct()

        verifyComplexWhere(table, engine, builder, querySqlBuilder)
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val querySqlBuilder = bundle.querySqlBuilder

        // build & compile
        val builder = QueryBuilderImpl(
                table, querySqlBuilder, ARG_PLACEHOLDER,
                ORDER_BY_ASCENDING_KEY, ORDER_BY_DESCENDING_KEY
        )
                .where()
                .eq(table.short, 5.toShort()).and()
                .eq(table.int, 6).and()
                .eq(table.long, 7L).and()
                .eq(table.float, 8F).and()
                .eq(table.double, 9.toDouble()).and()
                .eq(table.string, "test 10").and()
                .eq(table.blob, byteArrayOf(11)).and()
                .gt(table.nullableInt, null)

        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name,
                querySqlBuilder = querySqlBuilder,
                whereSections = listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "short"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "long"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "float"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "double"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "string"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "blob"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_int")
                )
        )

        val expectedQuery = QueryImpl(
                expectedSql, listOf(5.toShort(), 6, 7L, 8F, 9.0, "test 10",
                byteArrayOf(11), TypedNull(Type.INT))
        )

        // Verify
        assertEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(8)
        assertThat(statement.shortBoundAt(1)).isEqualTo(5.toShort())
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
        assertThat(statement.longBoundAt(3)).isEqualTo(7L)
        assertThat(statement.floatBoundAt(4)).isEqualTo(8F)
        assertThat(statement.doubleBoundAt(5)).isEqualTo(9.toDouble())
        assertThat(statement.stringBoundAt(6)).isEqualTo("test 10")
        assertThat(statement.blobBoundAt(7)).isEqualTo(byteArrayOf(11))
        assertThat(statement.isNullBoundAt(8)).isTrue()
    }


    private fun verifyComplexWhere(
            table: TableForTest, engine: EngineForTestingBuilders,
            adderOrEnder: AdderOrEnder<*, *>, querySqlBuilder: QuerySqlBuilder) {

        // build & compile
        val actualQuery = adderOrEnder.build()
        adderOrEnder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = querySqlBuilder, distinct = true,
                columns = listOf("string"),
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "double")),
                groupBy = listOf("blob"), orderBy = listOf(OrderInfoForTest("int", true)),
                limit = 5, offset = 8
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5.0))

        // Verify
        assertEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.doubleBoundAt(1)).isEqualTo(5.0)
    }


    private fun assertArgFreeStatement(
            table: TableForTest, engine: EngineForTestingBuilders,
            adderOrEnder: AdderOrEnder<*, *>, querySqlBuilder: QuerySqlBuilder,
            distinct: Boolean = false, columns: Iterable<String>? = null,
            whereSections: Iterable<WhereSection>? = null, groupBy: Iterable<String>? = null,
            orderBy: Iterable<OrderInfo>? = null, limit: Long? = null, offset: Long? = null) {

        // build & compile
        val actualQuery = adderOrEnder.build()
        adderOrEnder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = querySqlBuilder, distinct = distinct,
                columns = columns, whereSections = whereSections, groupBy = groupBy,
                orderBy = orderBy, limit = limit, offset = offset
        )

        val expectedQuery = QueryImpl(expectedSql, emptyList())

        // Verify
        assertEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).isEmpty()
    }


    private fun buildQuerySql(
            tableName: String, querySqlBuilder: QuerySqlBuilder,
            distinct: Boolean = false, columns: Iterable<String>? = null,
            whereSections: Iterable<WhereSection>? = null, groupBy: Iterable<String>? = null,
            orderBy: Iterable<OrderInfo>? = null, limit: Long? = null, offset: Long? = null):
            String {

        return querySqlBuilder.build(
                tableName = tableName, distinct = distinct, columns = columns,
                joinInfos = null, whereSections = whereSections, groupBy = groupBy,
                orderBy = orderBy, limit = limit, offset = offset,
                argPlaceholder = ARG_PLACEHOLDER,
                orderByAscendingKey = ORDER_BY_ASCENDING_KEY,
                orderByDescendingKey = ORDER_BY_DESCENDING_KEY
        )
    }



    private class Bundle(
            val table: TableForTest, val engine: EngineForTestingBuilders,
            val querySqlBuilder: QuerySqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(defaultTableConfiguration(engine))
                return Bundle(table, engine, QuerySqlBuilderForTesting())
            }
        }
    }



    private data class OrderInfoForTest(
            override val columnName: String, override val ascending: Boolean) :
            OrderInfo



    companion object {
        private const val ARG_PLACEHOLDER = "?"
        private const val ORDER_BY_ASCENDING_KEY = "ASC"
        private const val ORDER_BY_DESCENDING_KEY = "DESC"

        private fun assertEquality(actualQuery: Query, expectedQuery: Query) {
            assertThat(actualQuery.sql).isEqualTo(expectedQuery.sql)
            assertThat(actualQuery.arguments).containsExactlyElementsOf(expectedQuery.arguments)
        }
    }
}