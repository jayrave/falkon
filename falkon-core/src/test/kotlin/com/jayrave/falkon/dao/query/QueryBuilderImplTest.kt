package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledQueryForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QueryBuilderImplTest {

    @Test
    fun testQueryingWithoutSettingAnything() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(
                table, engineSpy, distinct = false, columns = null, whereClause = null,
                groupBy = null, having = null, orderBy = null, limit = null, offset = null
        )

        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testWithDistinct() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.distinct().query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, distinct = true)
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testWithSingleSelectedColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.select(table.int).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, columns = listOf("int"))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.select(table.int, table.string).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, columns = listOf("int", "string"))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testRedefiningSelectedColumnsOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.select(table.int).select(table.int).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, columns = listOf("int"))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.where().eq(table.int, 5).query()

        // Verify interactions with engine
        verifyCallToQuery(table, engineSpy, whereClause = "int = ?")

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engineSpy.compiledQueries.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testGroupByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.int).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, groupBy = listOf("int"))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.string, table.blob).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, groupBy = listOf("string", "blob"))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testRedefiningGroupByOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.int).groupBy(table.string).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, groupBy = listOf("string"))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testOrderByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, orderBy = listOf(Pair("int", true)))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).orderBy(table.blob, false).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(
                table, engineSpy,
                orderBy = listOf(Pair("int", true), Pair("blob", false))
        )

        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testSubsequentOrderByForTheSameColumnIsNoOp() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).orderBy(table.int, false).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, orderBy = listOf(Pair("int", true)))
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testLimit() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.limit(50).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, limit = 50)
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testOffset() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = QueryBuilderImpl(table)
        builder.offset(72).query()

        // Verify interactions with engine & compiled statement
        verifyCallToQuery(table, engineSpy, offset = 72)
        assertNoArgsWithStatementExecutionAndClosure(engineSpy)
    }


    @Test
    fun testComplexQueryWithWhereAtLast() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

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

        verifyComplexWhere(table, engineSpy)
    }


    @Test
    fun testComplexQueryWithWhereAtFirst() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

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

        verifyComplexWhere(table, engineSpy)
    }


    @Test
    fun testComplexQueryWithCrazyOrdering() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

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

        verifyComplexWhere(table, engineSpy)
    }


    private fun verifyCallToQuery(
            table: TableForTest, engineSpy: EngineForTestingBuilders, distinct: Boolean = false,
            columns: Iterable<String>? = null, whereClause: String? = null,
            groupBy: Iterable<String>? = null, having: String? = null,
            orderBy: Iterable<Pair<String, Boolean>>? = null,
            limit: Long? = null, offset: Long? = null) {

        verify(engineSpy).compileQuery(
                eq(table.name), eq(distinct),
                if (columns != null) eq(columns) else isNull<Iterable<String>>(),
                if (whereClause != null) eq(whereClause) else isNull<String>(),
                if (groupBy != null) eq(groupBy) else isNull<Iterable<String>>(),
                if (having != null) eq(having) else isNull<String>(),
                if (orderBy != null) eq(orderBy) else isNull<Iterable<Pair<String, Boolean>>>(),
                if (limit != null) eq(limit) else isNull<Long>(),
                if (offset != null) eq(offset) else isNull<Long>()
        )
    }


    private fun verifyComplexWhere(table: TableForTest, engineSpy: EngineForTestingBuilders) {
        // Verify interactions with engine
        verifyCallToQuery(
                table, engineSpy, distinct = true, columns = listOf("string"),
                whereClause = "double = ?", groupBy = listOf("blob"),
                orderBy = listOf(Pair("int", true)), limit = 5, offset = 8
        )

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engineSpy.compiledQueries.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.doubleBoundAt(1)).isEqualTo(5.0)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    private fun assertNoArgsWithStatementExecutionAndClosure(engineSpy: EngineForTestingBuilders) {
        assertThat(engineSpy.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engineSpy.compiledQueries.first()
        assertThat(statement.boundArgs).isEmpty()
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }



    private class Bundle(val table: TableForTest, val engineSpy: EngineForTestingBuilders) {
        companion object {
            fun default(engine: EngineForTestingBuilders =
            EngineForTestingBuilders.createWithOneShotStatements()): Bundle {

                val engineSpy = spy(engine)
                val table = TableForTest(defaultTableConfiguration(engineSpy))
                return Bundle(table, engineSpy)
            }
        }
    }
}