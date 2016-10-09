package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.delete.testLib.DeleteSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeleteBuilderImplTest {

    @Test
    fun testDeleteWithoutWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder, ARG_PLACEHOLDER)
        val actualDelete = deleteBuilder.build()
        deleteBuilder.compile()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(table.name, null, ARG_PLACEHOLDER)
        val expectedDelete = DeleteImpl(table.name, expectedSql, emptyList())

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        val statement = engine.compiledStatementsForDelete.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).isEmpty()
    }


    @Test
    fun testDeleteWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder, ARG_PLACEHOLDER)
        deleteBuilder.where().eq(table.int, 5)
        val actualDelete = deleteBuilder.build()
        deleteBuilder.compile()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name)),
                ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(table.name, expectedSql, listOf(5))

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        val statement = engine.compiledStatementsForDelete.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testDeleteViaWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder, ARG_PLACEHOLDER)
        deleteBuilder.where().eq(table.int, 5)
        val actualDelete = deleteBuilder.build()
        deleteBuilder.compile()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name)),
                ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(table.name, expectedSql, listOf(5))

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        val statement = engine.compiledStatementsForDelete.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testWhereGetsOverwrittenOnRedefining() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder, ARG_PLACEHOLDER)
        deleteBuilder.where().eq(table.int, 5)
        deleteBuilder.where().eq(table.string, "test")
        val actualDelete = deleteBuilder.build()
        deleteBuilder.compile()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name)),
                ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(table.name, expectedSql, listOf("test"))

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        val statement = engine.compiledStatementsForDelete.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.stringBoundAt(1)).isEqualTo("test")
    }


    @Test
    fun testDefiningWhereClauseDoesNotFireADeleteCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        DeleteBuilderImpl(table, deleteSqlBuilder, ARG_PLACEHOLDER).where().eq(table.int, 5)
        assertThat(engine.compiledStatementsForDelete).isEmpty()
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder, ARG_PLACEHOLDER)
        deleteBuilder.where()
                .eq(table.short, 5.toShort()).and()
                .eq(table.int, 6).and()
                .eq(table.long, 7L).and()
                .eq(table.float, 8F).and()
                .eq(table.double, 9.toDouble()).and()
                .eq(table.string, "test 10").and()
                .eq(table.blob, byteArrayOf(11)).and()
                .gt(table.nullableInt, null)

        val actualDelete = deleteBuilder.build()
        deleteBuilder.compile()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name,
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.short.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.long.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.float.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.double.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.blob.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.name)
                ), ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(
                table.name, expectedSql,
                listOf(5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11), TypedNull(Type.INT))
        )

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        val statement = engine.compiledStatementsForDelete.first()
        assertThat(statement.tableName).isEqualTo(table.name)
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
    
    
    
    private class Bundle(
            val table: TableForTest, val engine: EngineForTestingBuilders,
            val deleteSqlBuilder: DeleteSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, DeleteSqlBuilderForTesting())
            }
        }
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"

        private fun assertEquality(actualDelete: Delete, expectedDelete: Delete) {
            assertThat(actualDelete.tableName).isEqualTo(expectedDelete.tableName)
            assertThat(actualDelete.sql).isEqualTo(expectedDelete.sql)
            assertThat(actualDelete.arguments).containsExactlyElementsOf(expectedDelete.arguments)
        }
    }
}