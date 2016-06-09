package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledDeleteForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeleteBuilderImplTest {

    @Test
    fun testDeleteWithoutWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy
        
        val builder = DeleteBuilderImpl(table)
        builder.delete()

        // Verify interactions with engine
        verify(engineSpy).compileDelete(eq(table.name), isNull())
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engineSpy.compiledDeletes.first()
        assertThat(statement.boundArgs).isEmpty()
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testDeleteWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5)
        builder.delete()

        // Verify interactions with engine
        verify(engineSpy).compileDelete(eq(table.name), eq("int = ?"))
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engineSpy.compiledDeletes.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testDeleteViaWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5).delete()

        // Verify interactions with engine
        verify(engineSpy).compileDelete(eq(table.name), eq("int = ?"))
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engineSpy.compiledDeletes.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testWhereGetsOverwrittenOnRedefining() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5)
        builder.where().eq(table.string, "test").delete()

        // Verify interactions with engine
        verify(engineSpy).compileDelete(eq(table.name), eq("string = ?"))
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engineSpy.compiledDeletes.first()
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.stringBoundAt(1)).isEqualTo("test")
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testDefiningWhereClauseDoesNotFireADeleteCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5)
        verifyZeroInteractions(engineSpy)
    }


    @Test
    fun testDeleteReportsCorrectRowCount() {
        val numberOfRowsAffected = 112
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                deleteProvider = { OneShotCompiledDeleteForTest(numberOfRowsAffected) }
        )
        
        val bundle = Bundle.default(engine)
        val builder = DeleteBuilderImpl(bundle.table)
        assertThat(builder.delete()).isEqualTo(numberOfRowsAffected)
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engineSpy = bundle.engineSpy

        val builder = DeleteBuilderImpl(table)
        builder.where()
                .eq(table.short, 5.toShort()).and()
                .eq(table.int, 6).and()
                .eq(table.long, 7L).and()
                .eq(table.float, 8F).and()
                .eq(table.double, 9.toDouble()).and()
                .eq(table.string, "test").and()
                .eq(table.blob, byteArrayOf(10)).and()
                .gt(table.nullable, null)
                .delete()

        // Verify interactions with engine
        verify(engineSpy).compileDelete(
                eq(table.name),
                eq("short = ? AND int = ? AND long = ? AND float = ? AND double = ? AND " +
                        "string = ? AND blob = ? AND nullable > ?")
        )
        verifyNoMoreInteractions(engineSpy)

        // Verify interactions with compiled statement
        assertThat(engineSpy.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engineSpy.compiledDeletes.first()
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