package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledQueryForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.OrderInfo
import com.jayrave.falkon.engine.WhereSection
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
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
        builder.build()

        assertArgFreeStatement(table, engine, distinct = false)
    }


    @Test
    fun testWithDistinct() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.distinct().build()

        assertArgFreeStatement(table, engine, distinct = true)
    }


    @Test
    fun testWithSingleSelectedColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.select(table.int).build()

        assertArgFreeStatement(table, engine, columns = listOf("int"))
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.select(table.int, table.string).build()

        assertArgFreeStatement(
                table, engine, columns = listOf("int", "string")
        )
    }


    @Test
    fun testRedefiningSelectedColumnsOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.select(table.int).select(table.int).build()

        assertArgFreeStatement(table, engine, columns = listOf("int"))
    }


    @Test
    fun testWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.where().eq(table.int, 5).build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyQuerySql(
                tableName = table.name,
                whereSections = listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int"))
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testGroupByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.int).build()

        assertArgFreeStatement(table, engine, groupBy = listOf("int"))
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.string, table.blob).build()

        assertArgFreeStatement(
                table, engine, groupBy = listOf("string", "blob")
        )
    }


    @Test
    fun testRedefiningGroupByOverwritesExisting() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.groupBy(table.int).groupBy(table.string).build()

        assertArgFreeStatement(table, engine, groupBy = listOf("string"))
    }


    @Test
    fun testOrderByWithOneColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).build()

        assertArgFreeStatement(
                table, engine, orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.orderBy(table.int, true).orderBy(table.blob, false).build()

        assertArgFreeStatement(
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
        builder.orderBy(table.int, true).orderBy(table.int, false).build()

        assertArgFreeStatement(
                table, engine, orderBy = listOf(OrderInfoForTest("int", true))
        )
    }


    @Test
    fun testLimit() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.limit(50).build()

        assertArgFreeStatement(table, engine, limit = 50)
    }


    @Test
    fun testOffset() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.offset(72).build()

        assertArgFreeStatement(table, engine, offset = 72)
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
                .build()

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
                .build()

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
                .build()

        verifyComplexWhere(table, engine)
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = QueryBuilderImpl(table)
        builder.where()
                .eq(table.short, 5.toShort()).and()
                .eq(table.int, 6).and()
                .eq(table.long, 7L).and()
                .eq(table.float, 8F).and()
                .eq(table.double, 9.toDouble()).and()
                .eq(table.string, "test").and()
                .eq(table.blob, byteArrayOf(10)).and()
                .gt(table.nullable, null)
                .build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledQueries).hasSize(1)
        val statement: OneShotCompiledQueryForTest = engine.compiledQueries.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyQuerySql(
                table.name,
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
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable")
                )
        ))

        assertThat(statement.boundArgs).hasSize(8)
        assertThat(statement.shortBoundAt(1)).isEqualTo(5.toShort())
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
        assertThat(statement.longBoundAt(3)).isEqualTo(7L)
        assertThat(statement.floatBoundAt(4)).isEqualTo(8F)
        assertThat(statement.doubleBoundAt(5)).isEqualTo(9.toDouble())
        assertThat(statement.stringBoundAt(6)).isEqualTo("test")
        assertThat(statement.blobBoundAt(7)).isEqualTo(byteArrayOf(10))
        assertThat(statement.isNullBoundAt(8)).isTrue()
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
    }


    private fun assertArgFreeStatement(
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