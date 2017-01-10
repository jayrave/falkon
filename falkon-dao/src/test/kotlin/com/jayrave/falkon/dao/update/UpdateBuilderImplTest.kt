package com.jayrave.falkon.dao.update

import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.dao.update.testLib.UpdateSqlBuilderForTesting
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test
import com.jayrave.falkon.dao.testLib.NullableFlagPairConverter as NFPC

class UpdateBuilderImplTest {

    @Test
    fun `update without where`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder).values { set(table.int, 5) }
        val actualUpdate = builder.build()
        builder.compile()

        // build expected update
        val expectedSql = updateSqlBuilder.build(table.name, listOf(table.int.name), null)
        val expectedUpdate = UpdateImpl(table.name, expectedSql, listOf(5))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        val statement = engine.compiledStatementsForUpdate.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun `update with where`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder)
                .values { set(table.int, 5) }
                .where()
                .eq(table.string, "test")

        val actualUpdate = builder.build()
        builder.update()

        // build expected update
        val expectedSql = updateSqlBuilder.build(
                table.name, listOf(table.int.name),
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name))
        )

        val expectedUpdate = UpdateImpl(table.name, expectedSql, listOf(5, "test"))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        val statement = engine.compiledStatementsForUpdate.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
    }


    @Test
    fun `update multiple columns`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder).values {
            set(table.int, 5)
            set(table.string, "test")
        }

        val actualUpdate = builder.build()
        builder.update()

        // build expected insert
        val expectedSql = updateSqlBuilder.build(
                table.name, listOf(table.int.name, table.string.name), null
        )

        val expectedUpdate = UpdateImpl(table.name, expectedSql, listOf(5, "test"))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        val statement = engine.compiledStatementsForUpdate.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
    }


    @Test
    fun `update via adder or ender reports correct row count & compiled statement gets closed`() {
        testUpdateReportsCorrectRowCount { table: TableForTest ->
            UpdateBuilderImpl(table, UPDATE_SQL_BUILDER).values { set(table.int, 5) }.update()
        }
    }


    @Test
    fun `update via predicate adder or ender reports correct row count & compiled statement gets closed`() {
        testUpdateReportsCorrectRowCount { table: TableForTest ->
            UpdateBuilderImpl(table, UPDATE_SQL_BUILDER)
                    .values { set(table.int, 5) }
                    .where()
                    .eq(table.int, 6)
                    .update()
        }
    }


    @Test
    fun `compiled statement gets closed even if update via adder or ender throws`() {
        testStatementGetsClosedEvenIfUpdateThrows { table: TableForTest ->
            UpdateBuilderImpl(table, UPDATE_SQL_BUILDER).values { set(table.int, 5) }.update()
        }
    }


    @Test
    fun `compiled statement gets closed even if update via predicate adder or ender throws`() {
        testStatementGetsClosedEvenIfUpdateThrows { table: TableForTest ->
            UpdateBuilderImpl(table, UPDATE_SQL_BUILDER)
                    .values { set(table.int, 5) }
                    .where()
                    .eq(table.int, 6)
                    .update()
        }
    }


    @Test
    fun `setting value for an already set column, overrides the existing value`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = UpdateBuilderImpl(table, updateSqlBuilder).values {
            set(table.int, initialValue)
            set(table.int, overwritingValue)
        }

        val actualUpdate = builder.build()
        builder.update()

        // build expected insert
        val expectedSql = updateSqlBuilder.build(table.name, listOf(table.int.name), null)
        val expectedUpdate = UpdateImpl(table.name, expectedSql, listOf(overwritingValue))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        val statement = engine.compiledStatementsForUpdate.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(overwritingValue)
    }


    @Test
    fun `setting values for columns does not fire an update`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        UpdateBuilderImpl(table, updateSqlBuilder).values { set(table.int, 5) }
        assertThat(engine.compiledStatementsForUpdate).isEmpty()
    }


    @Test
    fun `all types are bound correctly`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder)
                .values {
                    set(table.short, 5.toShort())
                    set(table.int, 6)
                    set(table.long, 7L)
                    set(table.float, 8F)
                    set(table.double, 9.toDouble())
                    set(table.string, "test 10")
                    set(table.blob, byteArrayOf(11))
                    set(table.flagPair, FlagPair(false, true))
                    set(table.nullableShort, null)
                    set(table.nullableInt, null)
                    set(table.nullableLong, null)
                    set(table.nullableFloat, null)
                    set(table.nullableDouble, null)
                    set(table.nullableString, null)
                    set(table.nullableBlob, null)
                    set(table.nullableFlagPair, null)
                }.where()
                .eq(table.short, 12.toShort()).and()
                .eq(table.int, 13).and()
                .eq(table.long, 14L).and()
                .eq(table.float, 15F).and()
                .eq(table.double, 16.toDouble()).and()
                .eq(table.string, "test 17").and()
                .eq(table.blob, byteArrayOf(18)).and()
                .eq(table.flagPair, FlagPair(true, false)).and()
                .gt(table.nullableShort, null).and()
                .gt(table.nullableInt, null).and()
                .gt(table.nullableLong, null).and()
                .gt(table.nullableFloat, null).and()
                .gt(table.nullableDouble, null).and()
                .gt(table.nullableString, null).and()
                .gt(table.nullableBlob, null).and()
                .gt(table.nullableFlagPair, null)


        val actualUpdate = builder.build()
        builder.update()

        // build expected insert
        val expectedSql = updateSqlBuilder.build(
                table.name,
                listOf(
                        table.short.name, table.int.name, table.long.name, table.float.name,
                        table.double.name, table.string.name, table.blob.name, table.flagPair.name,
                        table.nullableShort.name, table.nullableInt.name, table.nullableLong.name,
                        table.nullableFloat.name, table.nullableDouble.name,
                        table.nullableString.name, table.nullableBlob.name,
                        table.nullableFlagPair.name
                ),
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
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.flagPair.name),
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
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableBlob.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableFlagPair.name)
                )
        )

        val expectedUpdate = UpdateImpl(
                table.name, expectedSql,
                listOf(
                        5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11),
                        NFPC.asShort(FlagPair(false, true)),
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB), TypedNull(NFPC.dbType),

                        12.toShort(), 13, 14L, 15F, 16.0, "test 17", byteArrayOf(18),
                        NFPC.asShort(FlagPair(true, false)),
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB), TypedNull(NFPC.dbType)
                )
        )

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledStatementsForUpdate).hasSize(1)
        val statement = engine.compiledStatementsForUpdate.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(32)
        assertThat(statement.shortBoundAt(1)).isEqualTo(5.toShort())
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
        assertThat(statement.longBoundAt(3)).isEqualTo(7L)
        assertThat(statement.floatBoundAt(4)).isEqualTo(8F)
        assertThat(statement.doubleBoundAt(5)).isEqualTo(9.toDouble())
        assertThat(statement.stringBoundAt(6)).isEqualTo("test 10")
        assertThat(statement.blobBoundAt(7)).isEqualTo(byteArrayOf(11))
        assertThat(statement.shortBoundAt(8)).isEqualTo(NFPC.asShort(FlagPair(false, true)))
        assertThat(statement.isNullBoundAt(9)).isTrue()
        assertThat(statement.isNullBoundAt(10)).isTrue()
        assertThat(statement.isNullBoundAt(11)).isTrue()
        assertThat(statement.isNullBoundAt(12)).isTrue()
        assertThat(statement.isNullBoundAt(13)).isTrue()
        assertThat(statement.isNullBoundAt(14)).isTrue()
        assertThat(statement.isNullBoundAt(15)).isTrue()
        assertThat(statement.isNullBoundAt(16)).isTrue()
        assertThat(statement.shortBoundAt(17)).isEqualTo(12.toShort())
        assertThat(statement.intBoundAt(18)).isEqualTo(13)
        assertThat(statement.longBoundAt(19)).isEqualTo(14L)
        assertThat(statement.floatBoundAt(20)).isEqualTo(15F)
        assertThat(statement.doubleBoundAt(21)).isEqualTo(16.toDouble())
        assertThat(statement.stringBoundAt(22)).isEqualTo("test 17")
        assertThat(statement.blobBoundAt(23)).isEqualTo(byteArrayOf(18))
        assertThat(statement.shortBoundAt(24)).isEqualTo(NFPC.asShort(FlagPair(true, false)))
        assertThat(statement.isNullBoundAt(25)).isTrue()
        assertThat(statement.isNullBoundAt(26)).isTrue()
        assertThat(statement.isNullBoundAt(27)).isTrue()
        assertThat(statement.isNullBoundAt(28)).isTrue()
        assertThat(statement.isNullBoundAt(29)).isTrue()
        assertThat(statement.isNullBoundAt(30)).isTrue()
        assertThat(statement.isNullBoundAt(31)).isTrue()
        assertThat(statement.isNullBoundAt(32)).isTrue()
    }



    private class Bundle(
            val table: TableForTest, val engine: EngineForTestingBuilders,
            val updateSqlBuilder: UpdateSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, UPDATE_SQL_BUILDER)
            }
        }
    }



    companion object {

        private val UPDATE_SQL_BUILDER = UpdateSqlBuilderForTesting()

        private fun assertEquality(actualUpdate: Update, expectedUpdate: Update) {
            assertThat(actualUpdate.tableName).isEqualTo(expectedUpdate.tableName)
            assertThat(actualUpdate.sql).isEqualTo(expectedUpdate.sql)
            assertThat(actualUpdate.arguments).containsExactlyElementsOf(expectedUpdate.arguments)
        }


        private fun testUpdateReportsCorrectRowCount(updateOp: (TableForTest) -> Int) {
            val numberOfRowsAffected = 8745
            val engine = EngineForTestingBuilders.createWithOneShotStatements(
                    updateProvider = { tableName, sql ->
                        IntReturningOneShotCompiledStatementForTest(
                                tableName, sql, numberOfRowsAffected
                        )
                    }
            )

            val table = TableForTest(configuration = defaultTableConfiguration(engine))
            assertThat(updateOp.invoke(table)).isEqualTo(numberOfRowsAffected)

            // Assert that the statement was executed and closed
            val statement = engine.compiledStatementsForUpdate.first()
            assertThat(statement.isExecuted).isTrue()
            assertThat(statement.isClosed).isTrue()
        }


        private fun testStatementGetsClosedEvenIfUpdateThrows(updateOp: (TableForTest) -> Int) {
            val engine = EngineForTestingBuilders.createWithOneShotStatements(
                    updateProvider = { tableName, sql ->
                        IntReturningOneShotCompiledStatementForTest(
                                tableName, sql, shouldThrowOnExecution = true
                        )
                    }
            )

            val exceptionWasThrown = try {
                updateOp.invoke(TableForTest(configuration = defaultTableConfiguration(engine)))
                false
            } catch (e: Exception) {
                true
            }

            when {
                !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
                else -> {
                    // Assert that the statement was not successfully executed but closed
                    val statement = engine.compiledStatementsForUpdate.first()
                    assertThat(statement.wasExecutionAttempted).isTrue()
                    assertThat(statement.isExecuted).isFalse()
                    assertThat(statement.isClosed).isTrue()
                }
            }
        }
    }
}