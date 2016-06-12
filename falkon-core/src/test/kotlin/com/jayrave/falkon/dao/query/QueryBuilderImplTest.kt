package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledQueryForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.OrderInfo
import com.jayrave.falkon.engine.WhereSection
import com.jayrave.falkon.engine.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QueryBuilderImplTest {

    @Test
    fun testQueryingWithoutSettingAnything() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.query()

        assertArgFreeStatementExecutionAndClosure(table, engine, distinct = false)
    }


    @Test
    fun testWithDistinct() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.distinct().query()

        assertArgFreeStatementExecutionAndClosure(table, engine, distinct = true)
    }


    @Test
    fun testWithSingleSelectedColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.select(table.int).query()

        assertArgFreeStatementExecutionAndClosure(table, engine, columns = listOf("int"))
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.select(table.int, table.string).query()

        assertArgFreeStatementExecutionAndClosure(
                table, engine, columns = listOf("int", "string")
        )
    }


    @Test
    fun testRedefiningSelectedColumnsOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.select(table.int).select(table.int).query()

        assertArgFreeStatementExecutionAndClosure(table, engine, columns = listOf("int"))
    }


    @Test
    fun testWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.where().eq(table.int, 5).query()

        // Verify interactions with compiled statement
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyQuerySql(
                tableName = table.name,
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int"))
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testGroupByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.int).query()

        assertArgFreeStatementExecutionAndClosure(table, engine, groupBy = listOf("int"))
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.string, table.blob).query()

        assertArgFreeStatementExecutionAndClosure(
                table, engine, groupBy = listOf("string", "blob")
        )
    }


    @Test
    fun testRedefiningGroupByOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.int).groupBy(table.string).query()

        assertArgFreeStatementExecutionAndClosure(table, engine, groupBy = listOf("string"))
    }


    @Test
    fun testOrderByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).query()

        assertArgFreeStatementExecutionAndClosure(
                table, engine, orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).orderBy(table.blob, false).query()

        assertArgFreeStatementExecutionAndClosure(
                table, engine,
                orderBy = listOf(OrderInfoForTest("int", true), OrderInfoForTest("blob", false))
        )
    }


    @Test
    fun testSubsequentOrderByForTheSameColumnIsNoOp() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).orderBy(table.int, false).query()

        assertArgFreeStatementExecutionAndClosure(
                table, engine, orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testLimit() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.limit(50).query()

        assertArgFreeStatementExecutionAndClosure(table, engine, limit = 50)
    }


    @Test
    fun testOffset() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.offset(72).query()

        assertArgFreeStatementExecutionAndClosure(table, engine, offset = 72)
    }


    @Test
    fun testComplexQueryWithWhereAtLast() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder
                .distinct()
                .select(table.string)
                .groupBy(table.blob)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)
                .where().eq(table.double, 5.0)
                .query()

        verifyComplexWhere(table, engine)
    }


    @Test
    fun testComplexQueryWithWhereAtFirst() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder
                .where().eq(table.double, 5.0)
                .distinct()
                .select(table.string)
                .groupBy(table.blob)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)
                .query()

        verifyComplexWhere(table, engine)
    }


    @Test
    fun testComplexQueryWithCrazyOrdering() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder
                .limit(5)
                .orderBy(table.int, true)
                .where().eq(table.double, 5.0)
                .offset(8)
                .groupBy(table.blob)
                .select(table.string)
                .distinct()
                .query()

        verifyComplexWhere(table, engine)
    }


    private fun verifyComplexWhere(table: TableForTest, engine: EngineForTestingBuilders) {
        // Verify interactions with compiled statement
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyQuerySql(
                tableName = table.name, distinct = true, columns = listOf("string"),
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "double")),
                groupBy = listOf("blob"), having = null,
                orderBy = listOf(OrderInfoForTest("int", true)), limit = 5, offset = 8
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.doubleBoundAt(1)).isEqualTo(5.0)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    private fun assertArgFreeStatementExecutionAndClosure(
            table: TableForTest, engine: EngineForTestingBuilders, distinct: Boolean = false,
            columns: Iterable<String>? = null, whereSections: Iterable<WhereSection>? = null,
            groupBy: Iterable<String>? = null, having: String? = null,
            orderBy: Iterable<OrderInfo>? = null,
            limit: Long? = null, offset: Long? = null) {

        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyQuerySql(
                tableName = table.name, distinct = distinct, columns = columns,
                whereSections = whereSections, groupBy = groupBy, having = having,
                orderBy = orderBy, limit = limit, offset = offset
        ))

        assertThat(statement.boundArgs).isEmpty()
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }



    private class Bundle(val table: TableForTest, val engine: EngineForTestingBuilders) {
        companion object {
            fun default(engine: EngineForTestingBuilders =
            EngineForTestingBuilders.createWithOneShotStatements()): Bundle {

                val table = TableForTest(defaultTableConfiguration(engine))
                return Bundle(table, engine)
            }
        }
    }



    private data class OrderInfoForTest(
            override val columnName: String, override val ascending: Boolean) :
            OrderInfo
}