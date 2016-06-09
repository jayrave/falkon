package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledInsertForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.testLib.iterableContainsInAnyOrder
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InsertBuilderImplTest {

    @Test
    fun testInsertWithSingleColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = InsertBuilderImpl(table)
        builder.set(table.int, 5).insert()

        // Verify interactions with engine
        verify(engineSpy).compileInsert(eq(table.name), iterableContainsInAnyOrder(table.int.name))
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engineSpy.compiledInserts.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testInsertWithMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = InsertBuilderImpl(table)
        builder.set(table.int, 5).set(table.string, "test").insert()

        // Verify interactions with engine
        verify(engineSpy).compileInsert(
                eq(table.name), iterableContainsInAnyOrder(table.int.name, table.string.name)
        )
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engineSpy.compiledInserts.first()
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testSetOverwritesExistingValueForTheSameColumn() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = InsertBuilderImpl(table)
        builder.set(table.int, initialValue).set(table.int, overwritingValue).insert()

        // Verify interactions with engine
        verify(engineSpy).compileInsert(eq(table.name), iterableContainsInAnyOrder(table.int.name))
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engineSpy.compiledInserts.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(overwritingValue)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testDefiningSetDoesNotFireAnUpdateCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = InsertBuilderImpl(table)
        builder.set(table.int, 5)
        verifyZeroInteractions(engineSpy)
    }


    @Test
    fun testInsertReturnsTrueForSingleRowInsertion() {
        testInsertReturnsAppropriateFlag(1, true)
    }


    @Test
    fun testInsertReturnsFalseForNonSingleRowInsertion() {
        testInsertReturnsAppropriateFlag(-1, false)
        testInsertReturnsAppropriateFlag(0, false)
        testInsertReturnsAppropriateFlag(2, false)
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = InsertBuilderImpl(table)
        builder
                .set(table.short, 5.toShort())
                .set(table.int, 6)
                .set(table.long, 7L)
                .set(table.float, 8F)
                .set(table.double, 9.toDouble())
                .set(table.string, "test")
                .set(table.blob, byteArrayOf(10))
                .set(table.nullable, null)
                .insert()

        // Verify interactions with engine
        verify(engineSpy).compileInsert(
                eq(table.name),
                iterableContainsInAnyOrder(
                        table.short.name, table.int.name, table.long.name, table.float.name,
                        table.double.name, table.string.name, table.blob.name, table.nullable.name
                )
        )
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledInserts).hasSize(1)
        val statement: OneShotCompiledInsertForTest = engineSpy.compiledInserts.first()
        assertThat(statement.boundArgs).hasSize(8)
        assertThat(statement.shortBoundAt(1)).isEqualTo(5.toShort())
        assertThat(statement.intBoundAt(2)).isEqualTo(6)
        assertThat(statement.longBoundAt(3)).isEqualTo(7L)
        assertThat(statement.floatBoundAt(4)).isEqualTo(8F)
        assertThat(statement.doubleBoundAt(5)).isEqualTo(9.toDouble())
        assertThat(statement.stringBoundAt(6)).isEqualTo("test")
        assertThat(statement.blobBoundAt(7)).isEqualTo(byteArrayOf(10))
        assertThat(statement.isNullBoundAt(8)).isTrue()
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    private fun testInsertReturnsAppropriateFlag(numberOfRowsInserted: Int, expectedFlag: Boolean) {
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                insertProvider = { OneShotCompiledInsertForTest(numberOfRowsInserted) }
        )

        val bundle = Bundle.default(engine)
        val table = bundle.table
        val builder = InsertBuilderImpl(table)
        assertThat(builder.set(table.int, 5).insert()).isEqualTo(expectedFlag)
    }



    private class Bundle(val table: TableForTest, val engineSpy: EngineForTestingBuilders) {
        companion object {
            fun default(engine: EngineForTestingBuilders =
            EngineForTestingBuilders.createWithOneShotStatements()): Bundle {

                val engineSpy = spy(engine)
                val table = TableForTest(defaultTableConfiguration(engineSpy))
                return Bundle(table, engineSpy)
            }
        }
    }
}