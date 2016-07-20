package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.dao.insert.testLib.InsertSqlBuilderForTesting
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledInsertForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InsertBuilderImplTest {

    @Test
    fun testInsertWithSingleColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val builder = InsertBuilderImpl(table, insertSqlBuilder, ARG_PLACEHOLDER).set(table.int, 5)
        val actualInsert = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(
                table.name, listOf(table.int.name), ARG_PLACEHOLDER
        )

        val expectedInsert = InsertImpl(expectedSql, listOf(5))

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testInsertWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val builder = InsertBuilderImpl(table, insertSqlBuilder, ARG_PLACEHOLDER)
                .set(table.int, 5)
                .set(table.string, "test")

        val actualInsert = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(
                table.name, listOf(table.int.name, table.string.name), ARG_PLACEHOLDER
        )

        val expectedInsert = InsertImpl(expectedSql, listOf(5, "test"))

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
    }


    @Test
    fun testSetOverwritesExistingValueForTheSameColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = InsertBuilderImpl(table, insertSqlBuilder, ARG_PLACEHOLDER)
                .set(table.int, initialValue)
                .set(table.int, overwritingValue)

        val actualInsert = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(
                table.name, listOf(table.int.name), ARG_PLACEHOLDER
        )

        val expectedInsert = InsertImpl(expectedSql, listOf(6))

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(6)
    }


    @Test
    fun testDefiningSetDoesNotFireAnUpdateCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        InsertBuilderImpl(table, insertSqlBuilder, ARG_PLACEHOLDER).set(table.int, 5)
        assertThat(engine.compiledInserts).isEmpty()
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val insertSqlBuilder = bundle.insertSqlBuilder

        // build & compile
        val builder = InsertBuilderImpl(table, insertSqlBuilder, ARG_PLACEHOLDER)
                .set(table.short, 5.toShort())
                .set(table.int, 6)
                .set(table.long, 7L)
                .set(table.float, 8F)
                .set(table.double, 9.toDouble())
                .set(table.string, "test 10")
                .set(table.blob, byteArrayOf(11))
                .set(table.nullableInt, null)

        val actualInsert = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = insertSqlBuilder.build(
                table.name,
                listOf(
                        table.short.name, table.int.name, table.long.name, table.float.name,
                        table.double.name, table.string.name, table.blob.name,
                        table.nullableInt.name
                ), ARG_PLACEHOLDER
        )

        val expectedInsert = InsertImpl(
                expectedSql,
                listOf(5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11), TypedNull(Type.INT))
        )

        // Verify
        assertEquality(actualInsert, expectedInsert)
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
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
            val insertSqlBuilder: InsertSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, InsertSqlBuilderForTesting())
            }
        }
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"

        private fun assertEquality(actualInsert: Insert, expectedInsert: Insert) {
            assertThat(actualInsert.sql).isEqualTo(expectedInsert.sql)
            assertThat(actualInsert.arguments).containsExactlyElementsOf(expectedInsert.arguments)
        }
    }
}