package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.delete.testLib.DeleteSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class DeleteBuilderImplTest {

    @Test
    fun `delete without where`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder)
        val actualDelete = deleteBuilder.build()
        deleteBuilder.delete()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(table.name, null)
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
    fun `delete with where`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder)
        deleteBuilder.where().eq(table.int, 5)
        val actualDelete = deleteBuilder.build()
        deleteBuilder.delete()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name))
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
    fun `delete via where`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val builder = DeleteBuilderImpl(table, deleteSqlBuilder).where().eq(table.int, 5)
        val actualDelete = builder.build()
        builder.delete()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name))
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
    fun `delete via delete builder reports correct row count & compiled statement gets closed`() {
        testDeleteReportsCorrectRowCountAndCompiledStatementGetsClosed { table: TableForTest ->
            DeleteBuilderImpl(table, DELETE_SQL_BUILDER).delete()
        }
    }


    @Test
    fun `delete via adder or ender reports correct row count & compiled statement gets closed`() {
        testDeleteReportsCorrectRowCountAndCompiledStatementGetsClosed { table: TableForTest ->
            DeleteBuilderImpl(table, DELETE_SQL_BUILDER)
                    .where()
                    .eq(table.int, 6)
                    .delete()
        }
    }


    @Test
    fun `compiled statement gets closed even if delete via delete builder throws`() {
        testStatementGetsClosedEvenIfDeleteThrows { table: TableForTest ->
            DeleteBuilderImpl(table, DELETE_SQL_BUILDER).delete()
        }
    }


    @Test
    fun `compiled statement gets closed even if delete via adder or ender throws`() {
        testStatementGetsClosedEvenIfDeleteThrows { table: TableForTest ->
            DeleteBuilderImpl(table, DELETE_SQL_BUILDER)
                    .where()
                    .eq(table.int, 6)
                    .delete()
        }
    }


    @Test
    fun `where gets overwritten on redefining`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        // build & compile
        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder)
        deleteBuilder.where().eq(table.int, 5)
        deleteBuilder.where().eq(table.string, "test")
        val actualDelete = deleteBuilder.build()
        deleteBuilder.delete()

        // build expected delete
        val expectedSql = deleteSqlBuilder.build(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name))
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
    fun `defining where clause does not fire delete`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        DeleteBuilderImpl(table, deleteSqlBuilder).where().eq(table.int, 5)
        assertThat(engine.compiledStatementsForDelete).isEmpty()
    }


    @Test
    fun `all types are bound correctly`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val deleteSqlBuilder = bundle.deleteSqlBuilder

        val deleteBuilder = DeleteBuilderImpl(table, deleteSqlBuilder)
        deleteBuilder.where()
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

        val actualDelete = deleteBuilder.build()
        deleteBuilder.delete()

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
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableShort.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableLong.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableFloat.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableDouble.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableString.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableBlob.name)
                )
        )

        val expectedDelete = DeleteImpl(
                table.name, expectedSql,
                listOf(
                        5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11),
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB)
                )
        )

        // Verify
        assertEquality(actualDelete, expectedDelete)
        assertThat(engine.compiledStatementsForDelete).hasSize(1)
        val statement = engine.compiledStatementsForDelete.first()
        assertThat(statement.tableName).isEqualTo(table.name)
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
    
    
    
    private class Bundle(
            val table: TableForTest, val engine: EngineForTestingBuilders,
            val deleteSqlBuilder: DeleteSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, DELETE_SQL_BUILDER)
            }
        }
    }



    companion object {

        private val DELETE_SQL_BUILDER = DeleteSqlBuilderForTesting()

        private fun assertEquality(actualDelete: Delete, expectedDelete: Delete) {
            assertThat(actualDelete.tableName).isEqualTo(expectedDelete.tableName)
            assertThat(actualDelete.sql).isEqualTo(expectedDelete.sql)
            assertThat(actualDelete.arguments).containsExactlyElementsOf(expectedDelete.arguments)
        }


        private fun testDeleteReportsCorrectRowCountAndCompiledStatementGetsClosed(
                deleteOp: (TableForTest) -> Int) {

            val numberOfRowsAffected = 8745
            val engine = EngineForTestingBuilders.createWithOneShotStatements(
                    deleteProvider = { tableName, sql ->
                        IntReturningOneShotCompiledStatementForTest(
                                tableName, sql, numberOfRowsAffected
                        )
                    }
            )

            val table = TableForTest(configuration = defaultTableConfiguration(engine))
            assertThat(deleteOp.invoke(table)).isEqualTo(numberOfRowsAffected)

            // Assert that the statement was not successfully executed but closed
            val statement = engine.compiledStatementsForDelete.first()
            assertThat(statement.isExecuted).isTrue()
            assertThat(statement.isClosed).isTrue()
        }


        private fun testStatementGetsClosedEvenIfDeleteThrows(deleteOp: (TableForTest) -> Int) {
            val engine = EngineForTestingBuilders.createWithOneShotStatements(
                    deleteProvider = { tableName, sql ->
                        IntReturningOneShotCompiledStatementForTest(
                                tableName, sql, shouldThrowOnExecution = true
                        )
                    }
            )

            var exceptionWasThrown = false
            try {
                deleteOp.invoke(TableForTest(configuration = defaultTableConfiguration(engine)))
            } catch (e: Exception) {
                exceptionWasThrown = true
            }

            when {
                !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
                else -> {
                    // Assert that the statement was not successfully executed but closed
                    val statement = engine.compiledStatementsForDelete.first()
                    assertThat(statement.wasExecutionAttempted).isTrue()
                    assertThat(statement.isExecuted).isFalse()
                    assertThat(statement.isClosed).isTrue()
                }
            }
        }
    }
}