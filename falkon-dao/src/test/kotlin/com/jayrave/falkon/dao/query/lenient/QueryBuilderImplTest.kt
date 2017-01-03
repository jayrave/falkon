package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.query.JoinType
import com.jayrave.falkon.dao.query.QueryImpl
import com.jayrave.falkon.dao.query.testLib.*
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.NoArgPredicate
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class QueryBuilderImplTest {

    @Test
    fun `with distinct`() {
        val bundle = Bundle.default()
        val builder = bundle.newBuilder()

        builder.fromTable(bundle.table).distinct()
        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, distinct = true)
    }


    @Test
    fun `selected column list if empty is nothing is explicitly selected`() {
        val bundle = Bundle.default()
        val builder = bundle.newBuilder()

        builder.fromTable(bundle.table)
        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder)
    }


    @Test
    fun `single column selection via raw string #select`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder.fromTable(table).select("custom_col")
        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(SelectColumnInfoForTest("custom_col", null))
        )
    }


    @Test
    fun `single column selection via type-safe #select`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder.fromTable(table).select(table.int)
        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(SelectColumnInfoForTest(table.int.qualifiedName, null))
        )
    }


    @Test
    fun `multiple column selection via both raw & type-safe #select`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .select(table.int)
                .select("custom_col_1")
                .select("custom_col_2")
                .select(table.string)
                .select("custom_col_3")

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(
                        SelectColumnInfoForTest(table.int.qualifiedName, null),
                        SelectColumnInfoForTest("custom_col_1", null),
                        SelectColumnInfoForTest("custom_col_2", null),
                        SelectColumnInfoForTest(table.string.qualifiedName, null),
                        SelectColumnInfoForTest("custom_col_3", null)
                )
        )
    }


    @Test
    fun `multiple column selection via both raw & type-safe #select with & without aliases`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .select(table.int, "t_int")
                .select("custom_col_1", "cc_1")
                .select("custom_col_2")
                .select(table.string)
                .select("custom_col_3", "cc_3")
                .select(table.nullableBlob, "t_n_blob")

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                columns = listOf(
                        SelectColumnInfoForTest(table.int.qualifiedName, "t_int"),
                        SelectColumnInfoForTest("custom_col_1", "cc_1"),
                        SelectColumnInfoForTest("custom_col_2", null),
                        SelectColumnInfoForTest(table.string.qualifiedName, null),
                        SelectColumnInfoForTest("custom_col_3", "cc_3"),
                        SelectColumnInfoForTest(table.nullableBlob.qualifiedName, "t_n_blob")
                )
        )
    }


    @Test
    fun `with where`() {
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

        val expectedQuery = QueryImpl(listOf(table.name), expectedSql, listOf(5))

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
    fun `group by with one column`() {
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
    fun `group by with multiple columns via single #groupBy call`() {
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
    fun `group by with multiple columns via multiple #groupBy calls`() {
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
    fun `order by with one column`() {
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
    fun `order by with multiple columns`() {
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
    fun `subsequent order by for the same column appends`() {
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
    fun `with limit`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .limit(50)

        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, limit = 50)
    }


    @Test
    fun `with offset`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .offset(72)

        assertArgFreeStatement(bundle = bundle, queryBuilderImpl = builder, offset = 72)
    }


    @Test
    fun `single default join without where`() {
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
    fun `single non default join without where`() {
        val bundle = Bundle.default()
        val table1 = bundle.table
        val table2 = TableForTest(name = "table_for_join")
        val builder = bundle.newBuilder()

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble, JoinType.RIGHT_OUTER_JOIN)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                joinInfos = listOf(JoinInfoForTest(
                        JoinInfo.Type.RIGHT_OUTER_JOIN, table1.int.qualifiedName,
                        table2.name, table2.nullableDouble.qualifiedName
                ))
        )
    }


    @Test
    fun `multiple joins without where`() {
        val bundle = Bundle.default()
        val table1 = bundle.table
        val table2 = TableForTest(name = "table_for_join_1")
        val table3 = TableForTest(name = "table_for_join_2")
        val table4 = TableForTest(name = "table_for_join_3")
        val builder = bundle.newBuilder()

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble)
                .join(table2.long, table3.string, JoinType.LEFT_OUTER_JOIN)
                .join(table3.blob, table4.float, JoinType.RIGHT_OUTER_JOIN)
                .join(table4.nullableShort, table2.nullableBlob, JoinType.INNER_JOIN)

        assertArgFreeStatement(
                bundle = bundle, queryBuilderImpl = builder,
                joinInfos = listOf(
                        JoinInfoForTest(
                                JoinInfo.Type.INNER_JOIN, table1.int.qualifiedName,
                                table2.name, table2.nullableDouble.qualifiedName
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.LEFT_OUTER_JOIN, table2.long.qualifiedName,
                                table3.name, table3.string.qualifiedName
                        ),
                        JoinInfoForTest(
                                JoinInfo.Type.RIGHT_OUTER_JOIN, table3.blob.qualifiedName,
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
    fun `join with where from multiple tables`() {
        val bundle = Bundle.default()
        val table1 = bundle.table
        val table2 = TableForTest(name = "table_for_join_1")
        val table3 = TableForTest(name = "table_for_join_2")
        val builder = bundle.newBuilder()

        builder
                .fromTable(table1)
                .join(table1.int, table2.nullableDouble)
                .join(table1.int, table3.nullableFloat, JoinType.LEFT_OUTER_JOIN)
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
                                JoinInfo.Type.LEFT_OUTER_JOIN, table1.int.qualifiedName,
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

        val expectedQuery = QueryImpl(
                listOf(table1.name, table2.name, table3.name), expectedSql, listOf(5F, 6L)
        )

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
    fun `complex query with where at last`() {
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
    fun `complex query with where at first`() {
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
    fun `complex query with crazy ordering`() {
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
    fun `with lots of options`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val tableForJoin = TableForTest("table_for_join")
        val builder = bundle.newBuilder()

        builder
                .fromTable(table)
                .distinct()
                .select(table.int, null)
                .select("custom_col_1", "cc_1")
                .select(tableForJoin.string, "t_string_123")
                .select("custom_col_2", null)
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
                columns = listOf(
                        SelectColumnInfoForTest(table.int.qualifiedName, null),
                        SelectColumnInfoForTest("custom_col_1", "cc_1"),
                        SelectColumnInfoForTest(tableForJoin.string.qualifiedName, "t_string_123"),
                        SelectColumnInfoForTest("custom_col_2", null)
                ),
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

        val expectedQuery = QueryImpl(
                listOf(table.name, tableForJoin.name), expectedSql, listOf(5.0, 6)
        )

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
    fun `all types are bound correctly`() {
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
                .gt(table.nullableShort, null).and()
                .gt(table.nullableInt, null).and()
                .gt(table.nullableLong, null).and()
                .gt(table.nullableFloat, null).and()
                .gt(table.nullableDouble, null).and()
                .gt(table.nullableString, null).and()
                .gt(table.nullableBlob, null)

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
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableShort.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableLong.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableFloat.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableDouble.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableString.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableBlob.qualifiedName)
                )
        )

        val expectedQuery = QueryImpl(
                listOf(table.name), expectedSql,
                listOf(
                        5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11),
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB)
                )
        )

        // Verify
        val engine = bundle.engine
        assertQueryEquality(actualQuery, expectedQuery)
        assertThat(engine.compiledStatementsForQuery).hasSize(1)
        val statement = engine.compiledStatementsForQuery.first()
        assertThat(statement.tableNames).containsOnly(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(14)
        assertThat(statement.shortBoundAt(1)).isEqualTo(5.toShort())
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
        assertThat(statement.longBoundAt(3)).isEqualTo(7L)
        assertThat(statement.floatBoundAt(4)).isEqualTo(8F)
        assertThat(statement.doubleBoundAt(5)).isEqualTo(9.toDouble())
        assertThat(statement.stringBoundAt(6)).isEqualTo("test 10")
        assertThat(statement.blobBoundAt(7)).isEqualTo(byteArrayOf(11))
        assertThat(statement.isNullBoundAt(8)).isTrue()
        assertThat(statement.isNullBoundAt(9)).isTrue()
        assertThat(statement.isNullBoundAt(10)).isTrue()
        assertThat(statement.isNullBoundAt(11)).isTrue()
        assertThat(statement.isNullBoundAt(12)).isTrue()
        assertThat(statement.isNullBoundAt(13)).isTrue()
        assertThat(statement.isNullBoundAt(14)).isTrue()
    }


    private fun verifyComplexWhere(bundle: Bundle, queryBuilderImpl: QueryBuilderImpl) {
        // build & compile
        val actualQuery = queryBuilderImpl.build()
        queryBuilderImpl.compile()

        // build expected query
        val table = bundle.table
        val expectedSql = buildQuerySql(
                tableName = table.name, querySqlBuilder = bundle.querySqlBuilder, distinct = true,
                columns = listOf(SelectColumnInfoForTest(table.string.qualifiedName, null)),
                whereSections = listOf(OneArgPredicate(
                        OneArgPredicate.Type.EQ, table.double.qualifiedName
                )),
                groupBy = listOf(table.blob.qualifiedName),
                orderBy = listOf(OrderInfoForTest(table.int.qualifiedName, true)),
                limit = 5, offset = 8
        )

        val expectedQuery = QueryImpl(listOf(table.name), expectedSql, listOf(5.0))

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
            columns: Iterable<SelectColumnInfo>? = null, joinInfos: Iterable<JoinInfo>? = null,
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

        val tableNames = HashSet<String>()
        tableNames.add(bundle.table.name)
        joinInfos?.forEach { tableNames.add(it.nameOfTableToJoin) }

        val expectedQuery = QueryImpl(tableNames, expectedSql, emptyList())
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
            return QueryBuilderImpl(querySqlBuilder)
        }
    }
}