package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.query.Query
import com.jayrave.falkon.dao.query.QueryImpl
import com.jayrave.falkon.dao.query.testLib.QuerySqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledQueryForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QueryBuilderImplTest {

    @Test
    fun testWithDistinct() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder.fromTable(table).distinct()
        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, distinct = true)
    }


    @Test
    fun testWithSingleSelectedColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder.fromTable(table).select(table.int)
        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, columns = listOf("int"))
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .select(table.int, table.string)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder, columns = listOf("int", "string")
        )
    }


    @Test
    fun testMultipleSelectCallsAppend() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .select(table.int)
                .select(table.string)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder, columns = listOf("int", "string")
        )
    }


    @Test
    fun testWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        // build & compile
        builder
                .fromTable(table)
                .where()
                .eq(table.int, 5)

        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = bundle.querySqlBuilder,
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int"))
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5))

        // Verify
        val engine = bundle.engine
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
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .groupBy(table.int)

        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, groupBy = listOf("int"))
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .groupBy(table.string, table.blob)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder, groupBy = listOf("string", "blob")
        )
    }


    @Test
    fun testMultipleGroupByCallsAppend() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .groupBy(table.int)
                .groupBy(table.string)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder, groupBy = listOf("int", "string")
        )
    }


    @Test
    fun testOrderByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .orderBy(table.int, true)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .orderBy(table.int, true)
                .orderBy(table.blob, false)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                orderBy = listOf(OrderInfoForTest("int", true), OrderInfoForTest("blob", false))
        )
    }


    @Test
    fun testSubsequentOrderByForTheSameColumnAppends() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .orderBy(table.int, true)
                .orderBy(table.int, false)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                orderBy = listOf(OrderInfoForTest("int", true), OrderInfoForTest("int", false))
        )
    }


    @Test
    fun testLimit() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .limit(50)

        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, limit = 50)
    }


    @Test
    fun testOffset() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .offset(72)

        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, offset = 72)
    }


    @Test
    fun testSingleJoinWithoutWhere() {
        val bundle = Bundle.default()
        val table1 = bundle.table
        val table2 = TableForTest(name = "table_for_join")
        val builder = bundle.newBuilder(qualifyColumnNames = true)

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, "${table1.name}.${table1.int.name}",
                        table2.name, "${table2.name}.${table2.nullableDouble.name}"
                ))
        )
    }


    @Test
    fun testMultiJoinWithoutWhere() {
        val bundle = Bundle.default()
        val table1 = bundle.table
        val table2 = TableForTest(name = "table_for_join_1")
        val table3 = TableForTest(name = "table_for_join_2")
        val table4 = TableForTest(name = "table_for_join_3")
        val builder = bundle.newBuilder(qualifyColumnNames = true)

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble)
                .join(table2.long, table3.string)
                .join(table3.blob, table4.float)
                .join(table4.nullableShort, table2.nullableBlob)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                joinInfos = listOf(
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, "${table1.name}.${table1.int.name}",
                                table2.name, "${table2.name}.${table2.nullableDouble.name}"
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, "${table2.name}.${table2.long.name}",
                                table3.name, "${table3.name}.${table3.string.name}"
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, "${table3.name}.${table3.blob.name}",
                                table4.name, "${table4.name}.${table4.float.name}"
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN,
                                "${table4.name}.${table4.nullableShort.name}",
                                table2.name, "${table2.name}.${table2.nullableBlob.name}"
                        )
                )
        )
    }


    @Test
    fun testComplexQueryWithWhereAtLast() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .distinct()
                .select(table.string)
                .groupBy(table.blob)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)
                .where().eq(table.double, 5.0)

        verifyComplexWhere(bundle, builder)
    }


    @Test
    fun testComplexQueryWithWhereAtFirst() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .where().eq(table.double, 5.0)
                .distinct()
                .select(table.string)
                .groupBy(table.blob)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)

        verifyComplexWhere(bundle, builder)
    }


    @Test
    fun testComplexQueryWithCrazyOrdering() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .limit(5)
                .orderBy(table.int, true)
                .where().eq(table.double, 5.0)
                .offset(8)
                .groupBy(table.blob)
                .select(table.string)
                .distinct()

        verifyComplexWhere(bundle, builder)
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        // build & compile
        builder
                .fromTable(table)
                .where()
                .eq(table.short, 5.toShort()).and()
                .eq(table.int, 6).and()
                .eq(table.long, 7L).and()
                .eq(table.float, 8F).and()
                .eq(table.double, 9.toDouble()).and()
                .eq(table.string, "test 10").and()
                .eq(table.blob, byteArrayOf(11)).and()
                .gt(table.nullableInt, null)

        // build & compile
        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name,
                querySqlBuilder = bundle.querySqlBuilder,
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
        val engine = bundle.engine
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


    private fun verifyComplexWhere(bundle: Bundle, queryBuilderImpl: QueryBuilderImpl) {
        // build & compile
        val actualQuery = queryBuilderImpl.build()
        queryBuilderImpl.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = bundle.table.name, querySqlBuilder = bundle.querySqlBuilder,
                distinct = true, columns = listOf("string"),
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "double")),
                groupBy = listOf("blob"), orderBy = listOf(OrderInfoForTest("int", true)),
                limit = 5, offset = 8
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5.0))

        // Verify
        val engine = bundle.engine
        assertEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.doubleBoundAt(1)).isEqualTo(5.0)
    }


    private fun assertArgFreeStatement(
            bundle: Bundle, queryBuilderImpl: QueryBuilderImpl, distinct: Boolean = false,
            columns: Iterable<String>? = null, joinInfos: Iterable<JoinInfo>? = null,
            whereSections: Iterable<WhereSection>? = null, groupBy: Iterable<String>? = null,
            orderBy: Iterable<OrderInfo>? = null, limit: Long? = null, offset: Long? = null) {

        // build & compile
        val actualQuery = queryBuilderImpl.build()
        queryBuilderImpl.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = bundle.table.name, querySqlBuilder = bundle.querySqlBuilder,
                distinct = distinct, columns = columns, joinInfos = joinInfos,
                whereSections = whereSections, groupBy = groupBy, orderBy = orderBy,
                limit = limit, offset = offset
        )

        val expectedQuery = QueryImpl(expectedSql, emptyList())

        // Verify
        val engine = bundle.engine
        assertEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).isEmpty()
    }


    private fun buildQuerySql(
            tableName: String, querySqlBuilder: QuerySqlBuilder, distinct: Boolean = false,
            columns: Iterable<String>? = null, joinInfos: Iterable<JoinInfo>? = null,
            whereSections: Iterable<WhereSection>? = null, groupBy: Iterable<String>? = null,
            orderBy: Iterable<OrderInfo>? = null, limit: Long? = null, offset: Long? = null):
            String {

        return querySqlBuilder.build(
                tableName = tableName, distinct = distinct, columns = columns,
                joinInfos = joinInfos, whereSections = whereSections, groupBy = groupBy,
                orderBy = orderBy, limit = limit, offset = offset,
                argPlaceholder = ARG_PLACEHOLDER
        )
    }



    private class Bundle(
            val table: TableForTest, val engine: EngineForTestingBuilders,
            val querySqlBuilder: QuerySqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                val querySqlBuilder = QuerySqlBuilderForTesting()
                return Bundle(table, engine, querySqlBuilder)
            }
        }
    }



    private data class JoinInfoForTest(
            override val type: JoinInfo.Type,
            override val qualifiedLocalColumnName: String,
            override val nameOfTableToJoin: String,
            override val qualifiedColumnNameFromTableToJoin: String) :
            JoinInfo



    private data class OrderInfoForTest(
            override val columnName: String, override val ascending: Boolean) :
            OrderInfo



    companion object {
        private const val ARG_PLACEHOLDER = "?"

        private fun Bundle.newBuilder(qualifyColumnNames: Boolean = false): QueryBuilderImpl {
            return QueryBuilderImpl(engine, querySqlBuilder, ARG_PLACEHOLDER, qualifyColumnNames)
        }


        private fun assertEquality(actualQuery: Query, expectedQuery: Query) {
            assertThat(actualQuery.sql).isEqualTo(expectedQuery.sql)
            assertThat(actualQuery.arguments).containsExactlyElementsOf(expectedQuery.arguments)
        }
    }
}