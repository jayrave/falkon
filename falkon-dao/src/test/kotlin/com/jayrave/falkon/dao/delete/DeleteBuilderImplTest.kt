package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.delete.testLib.DeleteSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledDeleteForTest
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
        val expectedDelete = DeleteImpl(expectedSql, emptyList())

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
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
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int")),
                ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(expectedSql, listOf(5))

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
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
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int")),
                ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(expectedSql, listOf(5))

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
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
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "string")),
                ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(expectedSql, listOf("test"))

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
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
        assertThat(engine.compiledDeletes).isEmpty()
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
                ), ARG_PLACEHOLDER
        )

        val expectedDelete = DeleteImpl(
                expectedSql, listOf(5.toShort(), 6, 7L, 8F, 9.0, "test 10",
                byteArrayOf(11), TypedNull(Type.INT))
        )

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
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
                val table = TableForTest(defaultTableConfiguration(engine))
                return Bundle(table, engine, DeleteSqlBuilderForTesting())
            }
        }
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"

        private fun assertEquality(actualDelete: Delete, expectedDelete: Delete) {
            assertThat(actualDelete.sql).isEqualTo(expectedDelete.sql)
            assertThat(actualDelete.arguments).containsExactlyElementsOf(expectedDelete.arguments)
        }
    }
}