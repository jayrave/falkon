package com.jayrave.falkon.dao.insertOrReplace

import com.jayrave.falkon.dao.insertOrReplace.testLib.InsertOrReplaceSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InsertOrReplaceBuilderImplCompiledStatementArgBindingTest {

    @Test
    fun `insert or replace compiled statement arg rebinding with multiple id & non id columns in correct order`() {
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
        val compiledStatement = builder.compile()

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

        // Rebind compiled statement in the same order the columns were defined
        compiledStatement.bindInt(1, 42)
        compiledStatement.bindString(2, "test 43")
        compiledStatement.bindBlob(3, byteArrayOf(44))
        compiledStatement.bindDouble(4, 45.0)

        // Compiled statement retained by engine should have the args bound in the same
        // order, as correct order was followed while setting columns
        assertThat(statement.boundArgs).hasSize(4)
        assertThat(statement.intBoundAt(1)).isEqualTo(42)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test 43")
        assertThat(statement.blobBoundAt(3)).isEqualTo(byteArrayOf(44))
        assertThat(statement.doubleBoundAt(4)).isEqualTo(45.0)
    }


    @Test
    fun `insert or replace compiled statement arg rebinding with multiple id & non id columns in mixed order`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values {
            set(table.int, 5)
            set(table.blob, byteArrayOf(6))
            set(table.string, "test 7")
            set(table.nullableFloat, 8F)
            set(table.nullableString, null)
            set(table.short, 9)
            set(table.nullableDouble, 10.0)
        }

        val actualInsertOrReplace = builder.build()
        val compiledStatement = builder.compile()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(
                table.name,
                listOf(
                        table.int.name, table.string.name,
                        table.nullableFloat.name, table.short.name
                ),
                listOf(table.blob.name, table.nullableString.name, table.nullableDouble.name)
        )

        // Id columns will be bound before non-id columns in the generated sql
        val expectedInsertOrReplace = InsertOrReplaceImpl(
                table.name, expectedSql,
                listOf(5, "test 7", 8F, 9.toShort(), byteArrayOf(6), TypedNull(Type.STRING), 10.0)
        )

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(7)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test 7")
        assertThat(statement.floatBoundAt(3)).isEqualTo(8F)
        assertThat(statement.shortBoundAt(4)).isEqualTo(9)
        assertThat(statement.blobBoundAt(5)).isEqualTo(byteArrayOf(6))
        assertThat(statement.isNullBoundAt(6)).isTrue()
        assertThat(statement.doubleBoundAt(7)).isEqualTo(10.0)

        // Rebind compiled statement in the same order the columns were defined
        compiledStatement.bindInt(1, 42)
        compiledStatement.bindBlob(2, byteArrayOf(43))
        compiledStatement.bindString(3, "test 44")
        compiledStatement.bindNull(4, Type.FLOAT)
        compiledStatement.bindString(5, "test 45")
        compiledStatement.bindShort(6, 46)
        compiledStatement.bindDouble(7, 47.0)

        // Even though args were bound via the statement in the order columns were set,
        // compiled statement retained by engine should have the args for id columns
        // bound before non id columns
        assertThat(statement.boundArgs).hasSize(7)
        assertThat(statement.intBoundAt(1)).isEqualTo(42)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test 44")
        assertThat(statement.isNullBoundAt(3)).isTrue()
        assertThat(statement.shortBoundAt(4)).isEqualTo(46)
        assertThat(statement.blobBoundAt(5)).isEqualTo(byteArrayOf(43))
        assertThat(statement.stringBoundAt(6)).isEqualTo("test 45")
        assertThat(statement.doubleBoundAt(7)).isEqualTo(47.0)
    }


    @Test
    fun `insert or replace compiled statement arg rebinding with multiple id & non id columns in reverse order`() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val sqlBuilder = bundle.sqlBuilder

        // build & compile
        val builder = InsertOrReplaceBuilderImpl(table, sqlBuilder).values {
            set(table.blob, byteArrayOf(5))
            set(table.nullableDouble, 6.0)
            set(table.int, 7)
            set(table.string, "test 8")
        }

        val actualInsertOrReplace = builder.build()
        val compiledStatement = builder.compile()

        // build expected insert or replace
        val expectedSql = sqlBuilder.build(
                table.name,
                listOf(table.int.name, table.string.name),
                listOf(table.blob.name, table.nullableDouble.name)
        )

        // Id columns will be bound before non-id columns in the generated sql
        val expectedInsertOrReplace = InsertOrReplaceImpl(
                table.name, expectedSql, listOf(7, "test 8", byteArrayOf(5), 6.0)
        )

        // Verify
        assertEquality(actualInsertOrReplace, expectedInsertOrReplace)
        assertThat(engine.compiledStatementsForInsertOrReplace).hasSize(1)
        val statement = engine.compiledStatementsForInsertOrReplace.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(4)
        assertThat(statement.intBoundAt(1)).isEqualTo(7)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test 8")
        assertThat(statement.blobBoundAt(3)).isEqualTo(byteArrayOf(5))
        assertThat(statement.doubleBoundAt(4)).isEqualTo(6.0)

        // Rebind compiled statement in the same order the columns were defined
        compiledStatement.bindBlob(1, byteArrayOf(42))
        compiledStatement.bindDouble(2, 43.0)
        compiledStatement.bindInt(3, 44)
        compiledStatement.bindString(4, "test 45")

        // Even though args were bound via the statement in the order columns were set,
        // compiled statement retained by engine should have the args for id columns
        // bound before non id columns
        assertThat(statement.boundArgs).hasSize(4)
        assertThat(statement.intBoundAt(1)).isEqualTo(44)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test 45")
        assertThat(statement.blobBoundAt(3)).isEqualTo(byteArrayOf(42))
        assertThat(statement.doubleBoundAt(4)).isEqualTo(43.0)
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
    }
}