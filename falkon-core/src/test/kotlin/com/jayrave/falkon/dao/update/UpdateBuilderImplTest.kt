package com.jayrave.falkon.dao.update

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledUpdateForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UpdateBuilderImplTest {

    @Test
    fun testUpdateWithoutWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = UpdateBuilderImpl(table)
        builder.set(table.int, 5).update()

        // Verify interactions with compiled statement
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyUpdateSql(
                table.name, listOf(table.int.name), null
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testUpdateWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = UpdateBuilderImpl(table)
        builder.set(table.int, 5).where().eq(table.string, "test").update()

        // Verify interactions with compiled statement
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyUpdateSql(
                table.name, listOf(table.int.name), "string = ?"
        ))

        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testCanUpdateMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = UpdateBuilderImpl(table)
        builder.set(table.int, 5).set(table.string, "test").update()

        // Verify interactions with compiled statement
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyUpdateSql(
                table.name, listOf(table.int.name, table.string.name), null
        ))

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
        val engine = bundle.engine

        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = UpdateBuilderImpl(table)
        builder.set(table.int, initialValue).set(table.int, overwritingValue).update()

        // Verify interactions with compiled statement
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyUpdateSql(
                table.name, listOf(table.int.name), null
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(overwritingValue)
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
    }


    @Test
    fun testDefiningSetAndWhereClausesDoesNotFireAnUpdateCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = UpdateBuilderImpl(table)
        builder.set(table.int, 5)
        assertThat(engine.compiledUpdates).isEmpty()
    }


    @Test
    fun testUpdateReportsCorrectRowCount() {
        val numberOfRowsAffected = 8745
        val engine = EngineForTestingBuilders.createWithOneShotStatements(
                updateProvider = { OneShotCompiledUpdateForTest(it, numberOfRowsAffected) }
        )

        val bundle = Bundle.default(engine)
        val table = bundle.table
        val builder = UpdateBuilderImpl(table)
        assertThat(builder.set(table.int, 5).update()).isEqualTo(numberOfRowsAffected)
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = UpdateBuilderImpl(table)
        builder
                .set(table.short, 5.toShort())
                .set(table.int, 6)
                .set(table.long, 7L)
                .set(table.float, 8F)
                .set(table.double, 9.toDouble())
                .set(table.string, "test")
                .set(table.blob, byteArrayOf(10))
                .set(table.nullable, null)
                .update()

        // Verify interactions with compiled statement
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyUpdateSql(
                table.name, listOf(table.short.name, table.int.name, table.long.name,
                table.float.name, table.double.name, table.string.name, table.blob.name,
                table.nullable.name), null
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
        assertThat(statement.isExecuted).isTrue()
        assertThat(statement.isClosed).isTrue()
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