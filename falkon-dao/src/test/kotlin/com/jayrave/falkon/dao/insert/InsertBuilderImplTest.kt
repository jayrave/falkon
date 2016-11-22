package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.dao.insert.testLib.InsertSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.*
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class InsertBuilderImplTest {

    @Test
    fun `insert with one column`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val builder = InsertBuilderImpl(table, insertSqlBuilder).values { set(table.int, 5) }
        val actualInsert = builder.build()
        builder.insert()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(table.name, listOf(table.int.name))
        val expectedInsert = InsertImpl(table.name, expectedSql, listOf(5))

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledStatementsForInsert).hasSize(1)
        val statement = engine.compiledStatementsForInsert.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun `insert with multiple columns`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val builder = InsertBuilderImpl(table, insertSqlBuilder).values {
            set(table.int, 5)
            set(table.string, "test")
        }

        val actualInsert = builder.build()
        builder.insert()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(
                table.name, listOf(table.int.name, table.string.name)
        )

        val expectedInsert = InsertImpl(table.name, expectedSql, listOf(5, "test"))

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledStatementsForInsert).hasSize(1)
        val statement = engine.compiledStatementsForInsert.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
    }


    @Test
    fun `insert throws if unexpected number of rows are affected & compiled statement gets closed`() {
        testInsertThrowsIfUnexpectedNumberOfRowsAreAffectedAndCompiledStatementIsClosed(-2, true)
        testInsertThrowsIfUnexpectedNumberOfRowsAreAffectedAndCompiledStatementIsClosed(-1, true)
        testInsertThrowsIfUnexpectedNumberOfRowsAreAffectedAndCompiledStatementIsClosed(0, true)
        testInsertThrowsIfUnexpectedNumberOfRowsAreAffectedAndCompiledStatementIsClosed(1, false)
        testInsertThrowsIfUnexpectedNumberOfRowsAreAffectedAndCompiledStatementIsClosed(2, true)
    }


    @Test
    fun `compiled statement gets closed even if insert throws`() {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                insertProvider = { tableName, sql ->
                    IntReturningOneShotCompiledStatementForTest(
                            tableName, sql, shouldThrowOnExecution = true
                    )
                }
        )

        val table = TableForTest(configuration = defaultTableConfiguration(engine))
        val builder = InsertBuilderImpl(table, INSERT_SQL_BUILDER)

        val exceptionWasThrown = try {
            builder.values { set(table.int, 5) }.insert()
            false
        } catch (e: Exception) {
            true
        }

        when {
            !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
            else -> {
                // Assert that the statement was not successfully executed but closed
                val statement = engine.compiledStatementsForInsert.first()
                assertThat(statement.wasExecutionAttempted).isTrue()
                assertThat(statement.isExecuted).isFalse()
                assertThat(statement.isClosed).isTrue()
            }
        }
    }


    @Test
    fun `setting value for an already set column, overrides the existing value`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = InsertBuilderImpl(table, insertSqlBuilder).values {
            set(table.int, initialValue)
            set(table.int, overwritingValue)
        }

        val actualInsert = builder.build()
        builder.insert()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(table.name, listOf(table.int.name))
        val expectedInsert = InsertImpl(table.name, expectedSql, listOf(6))

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledStatementsForInsert).hasSize(1)
        val statement = engine.compiledStatementsForInsert.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(6)
    }


    @Test
    fun `setting values for columns does not fire an insert`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        InsertBuilderImpl(table, insertSqlBuilder).values { set(table.int, 5) }
        assertThat(engine.compiledStatementsForInsert).isEmpty()
    }


    @Test
    fun `all types are bound correctly`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val builder = InsertBuilderImpl(table, insertSqlBuilder).values {
            set(table.short, 5.toShort())
            set(table.int, 6)
            set(table.long, 7L)
            set(table.float, 8F)
            set(table.double, 9.toDouble())
            set(table.string, "test 10")
            set(table.blob, byteArrayOf(11))
            set(table.nullableShort, null)
            set(table.nullableInt, null)
            set(table.nullableLong, null)
            set(table.nullableFloat, null)
            set(table.nullableDouble, null)
            set(table.nullableString, null)
            set(table.nullableBlob, null)
        }

        val actualInsert = builder.build()
        builder.insert()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(
                table.name,
                listOf(
                        table.short.name, table.int.name, table.long.name, table.float.name,
                        table.double.name, table.string.name, table.blob.name,
                        table.nullableShort.name, table.nullableInt.name, table.nullableLong.name,
                        table.nullableFloat.name, table.nullableDouble.name,
                        table.nullableString.name, table.nullableBlob.name
                )
        )

        val expectedInsert = InsertImpl(
                table.name, expectedSql,
                listOf(
                        5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11),
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB)
                )
        )

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledStatementsForInsert).hasSize(1)
        val statement = engine.compiledStatementsForInsert.first()
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
            val insertSqlBuilder: InsertSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, INSERT_SQL_BUILDER)
            }
        }
    }



    companion object {

        private val INSERT_SQL_BUILDER = InsertSqlBuilderForTesting()

        private fun assertEquality(actualInsert: Insert, expectedInsert: Insert) {
            assertThat(actualInsert.tableName).isEqualTo(expectedInsert.tableName)
            assertThat(actualInsert.sql).isEqualTo(expectedInsert.sql)
            assertThat(actualInsert.arguments).containsExactlyElementsOf(expectedInsert.arguments)
        }


        private fun testInsertThrowsIfUnexpectedNumberOfRowsAreAffectedAndCompiledStatementIsClosed(
                numberOfRowsInserted: Int, shouldThrow: Boolean) {

            val engine = EngineForTestingBuilders.createWithOneShotStatements(
                    insertProvider = { tableName, sql ->
                        IntReturningOneShotCompiledStatementForTest(
                                tableName, sql, numberOfRowsInserted
                        )
                    }
            )

            val table = TableForTest(configuration = defaultTableConfiguration(engine))
            val builder = InsertBuilderImpl(table, INSERT_SQL_BUILDER)

            val exceptionThrown = try {
                assertThat(builder.values { set(table.int, 5) }.insert())
                false
            } catch (e: Exception) {
                true
            }

            // Assert that the statement was executed and closed
            val statement = engine.compiledStatementsForInsert.first()
            assertThat(statement.isExecuted).isTrue()
            assertThat(statement.isClosed).isTrue()

            // Assert exception was thrown if it was supposed to
            assertThat(exceptionThrown).isEqualTo(shouldThrow)
        }
    }
}