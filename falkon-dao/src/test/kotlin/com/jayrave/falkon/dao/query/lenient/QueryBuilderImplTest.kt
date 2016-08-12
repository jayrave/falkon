package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.query.QueryImpl
import com.jayrave.falkon.dao.query.testLib.*
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.NoArgPredicate
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

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
        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(table.int.qualifiedName)
        )
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
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(table.int.qualifiedName, table.string.qualifiedName)
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
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(table.int.qualifiedName, table.string.qualifiedName)
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
                whereSections = listOf(OneArgPredicate(
                        OneArgPredicate.Type.EQ, table.int.qualifiedName
                ))
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5))

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name)
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

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                groupBy = listOf(table.int.qualifiedName)
        )
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
                bundle = bundle, queryBuilderImpl = builder,
                groupBy = listOf(table.string.qualifiedName, table.blob.qualifiedName)
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
                bundle = bundle, queryBuilderImpl = builder,
                groupBy = listOf(table.int.qualifiedName, table.string.qualifiedName)
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
                orderBy = listOf(OrderInfoForTest(table.int.qualifiedName, true))
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
                orderBy = listOf(
                        OrderInfoForTest(table.int.qualifiedName, true),
                        OrderInfoForTest(table.blob.qualifiedName, false)
                )
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
                orderBy = listOf(
                        OrderInfoForTest(table.int.qualifiedName, true),
                        OrderInfoForTest(table.int.qualifiedName, false)
                )
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
        val builder = bundle.newBuilder()

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, table1.int.qualifiedName,
                        table2.name, table2.nullableDouble.qualifiedName
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
        val builder = bundle.newBuilder()

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
                                JoinInfo.Type.INNER_JOIN, table1.int.qualifiedName,
                                table2.name, table2.nullableDouble.qualifiedName
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, table2.long.qualifiedName,
                                table3.name, table3.string.qualifiedName
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, table3.blob.qualifiedName,
                                table4.name, table4.float.qualifiedName
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, table4.nullableShort.qualifiedName,
                                table2.name, table2.nullableBlob.qualifiedName
                        )
                )
        )
    }


    @Test
    fun testJoinWithWhereFromMultipleTables() {
        val bundle = Bundle.default()
        val table1 = bundle.table
        val table2 = TableForTest(name = "table_for_join_1")
        val table3 = TableForTest(name = "table_for_join_2")
        val builder = bundle.newBuilder()

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble)
                .join(table1.int, table3.nullableFloat)
                .where()
                .eq(table1.float, 5F)
                .and()
                .gt(table2.nullableLong, 6)
                .or()
                .isNull(table3.nullableString)

        // build & compile
        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = bundle.table.name, querySqlBuilder = bundle.querySqlBuilder,
                joinInfos = listOf(
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, table1.int.qualifiedName,
                                table2.name, table2.nullableDouble.qualifiedName
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, table1.int.qualifiedName,
                                table3.name, table3.nullableFloat.qualifiedName
                        )
                ),
                whereSections = listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table1.float.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table2.nullableLong.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, table3.nullableString.qualifiedName)
                )
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5F, 6L))

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table1.name, table2.name, table3.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.floatBoundAt(1)).isEqualTo(5F)
        assertThat(statement.longBoundAt(2)).isEqualTo(6L)
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
    fun testQueryWithAllOptions() {
        val bundle = Bundle.default()
        val table = bundle.table
        val tableForJoin = TableForTest("table_for_join")
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .distinct()
                .select(table.int, tableForJoin.string)
                .join(table.long, tableForJoin.blob)
                .where().eq(table.double, 5.0).and().notEq(tableForJoin.int, 6)
                .groupBy(table.blob, tableForJoin.nullableInt)
                .orderBy(table.nullableFloat, true)
                .orderBy(tableForJoin.int, false)
                .limit(5)
                .offset(8)

        // build & compile
        val actualQuery = builder.build()
        builder.compile()

        // build expected query
        val expectedSql = buildQuerySql(
                tableName = table.name,
                querySqlBuilder = bundle.querySqlBuilder,
                distinct = true,
                columns = listOf(table.int.qualifiedName, tableForJoin.string.qualifiedName),
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.INNER_JOIN, table.long.qualifiedName,
                        tableForJoin.name, tableForJoin.blob.qualifiedName
                )),
                whereSections = listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.double.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, tableForJoin.int.qualifiedName)
                ),
                groupBy = listOf(table.blob.qualifiedName, tableForJoin.nullableInt.qualifiedName),
                orderBy = listOf(
                        OrderInfoForTest(table.nullableFloat.qualifiedName, true),
                        OrderInfoForTest(tableForJoin.int.qualifiedName, false)
                ),
                limit = 5, offset = 8
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5.0, 6))

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name, tableForJoin.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.doubleBoundAt(1)).isEqualTo(5.0)
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
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
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.short.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.int.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.long.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.float.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.double.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.string.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.blob.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.qualifiedName)
                )
        )

        val expectedQuery = QueryImpl(
                expectedSql, listOf(5.toShort(), 6, 7L, 8F, 9.0, "test 10",
                byteArrayOf(11), TypedNull(Type.INT))
        )

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name)
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
        val table = bundle.table
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = bundle.querySqlBuilder,
                distinct = true, columns = listOf(table.string.qualifiedName),
                whereSections = listOf(OneArgPredicate(
                        OneArgPredicate.Type.EQ, table.double.qualifiedName
                )),
                groupBy = listOf(table.blob.qualifiedName),
                orderBy = listOf(OrderInfoForTest(table.int.qualifiedName, true)),
                limit = 5, offset = 8
        )

        val expectedQuery = QueryImpl(expectedSql, listOf(5.0))

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name)
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
        val allConcernedTables = HashSet<String>()
        allConcernedTables.add(bundle.table.name)
        joinInfos?.forEach { allConcernedTables.add(it.nameOfTableToJoin) }

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnlyElementsOf(allConcernedTables)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).isEmpty()
    }



    companion object {
        private fun Bundle.newBuilder(): QueryBuilderImpl {
            return QueryBuilderImpl(querySqlBuilder, ARG_PLACEHOLDER)
        }
    }
}