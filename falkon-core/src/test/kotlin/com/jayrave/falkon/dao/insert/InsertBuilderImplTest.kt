package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledInsertForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InsertBuilderImplTest {

    @Test
    fun testInsertWithSingleColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = InsertBuilderImpl(table)
        builder.set(table.int, 5).build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(
                EngineForTestingBuilders.buildDummyInsertSql(table.name, listOf(table.int.name))
        )

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testInsertWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = InsertBuilderImpl(table)
        builder.set(table.int, 5).set(table.string, "test").build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyInsertSql(
                table.name, listOf(table.int.name, table.string.name)
        ))

        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
    }


    @Test
    fun testSetOverwritesExistingValueForTheSameColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = InsertBuilderImpl(table)
        builder.set(table.int, initialValue).set(table.int, overwritingValue).build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(
                EngineForTestingBuilders.buildDummyInsertSql(table.name, listOf(table.int.name))
        )

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(overwritingValue)
    }


    @Test
    fun testDefiningSetDoesNotFireAnUpdateCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = InsertBuilderImpl(table)
        builder.set(table.int, 5)
        assertThat(engine.compiledInserts).isEmpty()
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = InsertBuilderImpl(table)
        builder
                .set(table.short, 5.toShort())
                .set(table.int, 6)
                .set(table.long, 7L)
                .set(table.float, 8F)
                .set(table.double, 9.toDouble())
                .set(table.string, "test")
                .set(table.blob, byteArrayOf(10))
                .set(table.nullableInt, null)
                .build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engine.compiledInserts.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyInsertSql(
                table.name, listOf(table.short.name, table.int.name, table.long.name,
                table.float.name, table.double.name, table.string.name, table.blob.name,
                table.nullableInt.name)
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



    private class Bundle(val table: TableForTest, val engine: EngineForTestingBuilders) {
        companion object {
            fun default(engine: EngineForTestingBuilders =
            EngineForTestingBuilders.createWithOneShotStatements()): Bundle {

                val table = TableForTest(defaultTableConfiguration(engine))
                return Bundle(table, engine)
            }
        }
    }
}