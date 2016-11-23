package com.jayrave.falkon.dao.insertOrReplace

import com.jayrave.falkon.dao.insertOrReplace.testLib.InsertOrReplaceSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.IntReturningOneShotCompiledStatementForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.Test

class InsertOrReplaceBuilderImplTest {

    @Test
    fun `insert or replace with one id column`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values { set(table.int, 5) }
        val actualInsertOrReplace = builder.build()
        builder.insertOrReplace()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(table.name, listOf(table.int.name), emptyList())
        val expectedInsertOrReplace = InsertOrReplaceImpl(table.name, expectedSql, listOf(5))

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun `insert or replace with one non id column`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values { set(table.float, 5F) }
        val actualInsertOrReplace = builder.build()
        builder.insertOrReplace()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(table.name, emptyList(), listOf(table.float.name))
        val expectedInsertOrReplace = InsertOrReplaceImpl(table.name, expectedSql, listOf(5F))

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.floatBoundAt(1)).isEqualTo(5F)
    }


    @Test
    fun `insert or replace with multiple id columns followed by multiple non id columns`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values {
            set(table.int, 5)
            set(table.string, "test 6")
            set(table.blob, byteArrayOf(7))
            set(table.nullableDouble, 8.0)
        }

        val actualInsertOrReplace = builder.build()
        builder.insertOrReplace()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(
                table.name, listOf(table.int.name, table.string.name),
                listOf(table.blob.name, table.nullableDouble.name)
        )

        val expectedInsertOrReplace = InsertOrReplaceImpl(
                table.name, expectedSql, listOf(5, "test 6", byteArrayOf(7), 8.0)
        )

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(4)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test 6")
        assertThat(statement.blobBoundAt(3)).isEqualTo(byteArrayOf(7))
        assertThat(statement.doubleBoundAt(4)).isEqualTo(8.0)
    }


    @Test
    fun `insert or replace throws if unexpected number of rows are affected & compiled statement gets closed`() {
        testInsertOrReplaceThrowsIfUnexpectedNumberOfRowsAreAffectedAndCsIsClosed(-2, true)
        testInsertOrReplaceThrowsIfUnexpectedNumberOfRowsAreAffectedAndCsIsClosed(-1, true)
        testInsertOrReplaceThrowsIfUnexpectedNumberOfRowsAreAffectedAndCsIsClosed(0, true)
        testInsertOrReplaceThrowsIfUnexpectedNumberOfRowsAreAffectedAndCsIsClosed(1, false)
        testInsertOrReplaceThrowsIfUnexpectedNumberOfRowsAreAffectedAndCsIsClosed(2, true)
    }


    @Test
    fun `compiled statement gets closed even if insert or replace throws`() {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                insertOrReplaceProvider = { tableName, sql ->
                    IntReturningOneShotCompiledStatementForTest(
                            tableName, sql, shouldThrowOnExecution = true
                    )
                }
        )

        val table = TableForTest(configuration = defaultTableConfiguration(engine))
        val builder = InsertOrReplaceBuilderImpl(table, INSERT_OR_REPLACE_SQL_BUILDER)

        val exceptionWasThrown = try {
            builder.values { set(table.int, 5) }.insertOrReplace()
            false
        } catch (e: Exception) {
            true
        }

        when {
            !exceptionWasThrown -> failBecauseExceptionWasNotThrown(Exception::class.java)
            else -> {
                // Assert that the statement was not successfully executed but closed
                val statement = engine.compiledStatementsForInsertOrReplace.first()
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
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values {
            set(table.int, initialValue)
            set(table.int, overwritingValue)
            set(table.float, initialValue.toFloat())
            set(table.float, overwritingValue.toFloat())
        }

        val actualInsertOrReplace = builder.build()
        builder.insertOrReplace()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(
                table.name, listOf(table.int.name), listOf(table.float.name)
        )

        val expectedInsertOrReplace = InsertOrReplaceImpl(table.name, expectedSql, listOf(6, 6F))

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(6)
        assertThat(statement.floatBoundAt(2)).isEqualTo(6F)
    }


    @Test
    fun `setting values for columns does not fire insert or replace`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        InsertOrReplaceBuilderImpl(table, sqlBuilder).values {
            set(table.int, 5)
            set(table.nullableString, null)
        }

        assertThat(engine.compiledStatementsForInsertOrReplace).isEmpty()
    }


    @Test
    fun `all types are bound correctly`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values {
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

        val actualInsertOrReplace = builder.build()
        builder.insertOrReplace()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(
                table.name,
                listOf(
                        table.short.name, table.int.name,
                        table.string.name, table.nullableFloat.name
                ),

                listOf(
                        table.long.name, table.float.name, table.double.name, table.blob.name,
                        table.nullableShort.name, table.nullableInt.name, table.nullableLong.name,
                        table.nullableDouble.name, table.nullableString.name,
                        table.nullableBlob.name
                )
        )

        val expectedInsertOrReplace = InsertOrReplaceImpl(
                table.name, expectedSql,
                listOf(
                        5.toShort(), 6, "test 10", TypedNull(Type.FLOAT), 7L, 8F, 9.0,
                        byteArrayOf(11), TypedNull(Type.SHORT), TypedNull(Type.INT),
                        TypedNull(Type.LONG), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB)
                )
        )

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(14)
        assertThat(statement.shortBoundAt(1)).isEqualTo(5.toShort())
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
        assertThat(statement.stringBoundAt(3)).isEqualTo("test 10")
        assertThat(statement.isNullBoundAt(4)).isTrue()
        assertThat(statement.longBoundAt(5)).isEqualTo(7L)
        assertThat(statement.floatBoundAt(6)).isEqualTo(8F)
        assertThat(statement.doubleBoundAt(7)).isEqualTo(9.toDouble())
        assertThat(statement.blobBoundAt(8)).isEqualTo(byteArrayOf(11))
        assertThat(statement.isNullBoundAt(9)).isTrue()
        assertThat(statement.isNullBoundAt(10)).isTrue()
        assertThat(statement.isNullBoundAt(11)).isTrue()
        assertThat(statement.isNullBoundAt(12)).isTrue()
        assertThat(statement.isNullBoundAt(13)).isTrue()
        assertThat(statement.isNullBoundAt(14)).isTrue()
    }



    private class Bundle(
            val table: TableForTest, val engine: EngineForTestingBuilders,
            val sqlBuilder: InsertOrReplaceSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, INSERT_OR_REPLACE_SQL_BUILDER)
            }
        }
    }



    companion object {

        private val INSERT_OR_REPLACE_SQL_BUILDER = InsertOrReplaceSqlBuilderForTesting()

        private fun assertEquality(actual: InsertOrReplace, expected: InsertOrReplace) {
            assertThat(actual.tableName).isEqualTo(expected.tableName)
            assertThat(actual.sql).isEqualTo(expected.sql)
            assertThat(actual.arguments).containsExactlyElementsOf(expected.arguments)
        }


        /**
         * CS stands for compiled statement
         */
        private fun testInsertOrReplaceThrowsIfUnexpectedNumberOfRowsAreAffectedAndCsIsClosed(
                numberOfRowsInsertedOrReplaced: Int, shouldThrow: Boolean) {

            val engine = EngineForTestingBuilders.createWithOneShotStatements(
                    insertOrReplaceProvider = { tableName, sql ->
                        IntReturningOneShotCompiledStatementForTest(
                                tableName, sql, numberOfRowsInsertedOrReplaced
                        )
                    }
            )

            val table = TableForTest(configuration = defaultTableConfiguration(engine))
            val builder = InsertOrReplaceBuilderImpl(table, INSERT_OR_REPLACE_SQL_BUILDER)

            val exceptionThrown = try {
                assertThat(builder.values { set(table.int, 5) }.insertOrReplace())
                false
            } catch (e: Exception) {
                true
            }

            // Assert that the statement was executed and closed
            val statement = engine.compiledStatementsForInsertOrReplace.first()
            assertThat(statement.isExecuted).isTrue()
            assertThat(statement.isClosed).isTrue()

            // Assert exception was thrown if it was supposed to
            assertThat(exceptionThrown).isEqualTo(shouldThrow)
        }
    }
}