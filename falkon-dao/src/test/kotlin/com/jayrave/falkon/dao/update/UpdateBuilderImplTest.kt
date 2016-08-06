package com.jayrave.falkon.dao.update

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledUpdateForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.dao.update.testLib.UpdateSqlBuilderForTesting
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UpdateBuilderImplTest {

    @Test
    fun testUpdateWithoutWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder, ARG_PLACEHOLDER).set(table.int, 5)
        val actualUpdate = builder.build()
        builder.compile()

        // build expected update
        val expectedSql = updateSqlBuilder.build(
                table.name, listOf(table.int.name), null, ARG_PLACEHOLDER
        )

        val expectedUpdate = UpdateImpl(expectedSql, listOf(5))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testUpdateWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder, ARG_PLACEHOLDER)
                .set(table.int, 5)
                .where()
                .eq(table.string, "test")

        val actualUpdate = builder.build()
        builder.compile()

        // build expected update
        val expectedSql = updateSqlBuilder.build(
                table.name, listOf(table.int.name),
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name)),
                ARG_PLACEHOLDER
        )

        val expectedUpdate = UpdateImpl(expectedSql, listOf(5, "test"))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(2)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
        assertThat(statement.stringBoundAt(2)).isEqualTo("test")
    }


    @Test
    fun testCanUpdateMultipleColumns() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder, ARG_PLACEHOLDER)
                .set(table.int, 5)
                .set(table.string, "test")

        val actualUpdate = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = updateSqlBuilder.build(
                table.name, listOf(table.int.name, table.string.name), null, ARG_PLACEHOLDER
        )

        val expectedUpdate = UpdateImpl(expectedSql, listOf(5, "test"))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.tableName).isEqualTo(table.name)
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
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val builder = UpdateBuilderImpl(table, updateSqlBuilder, ARG_PLACEHOLDER)
                .set(table.int, initialValue)
                .set(table.int, overwritingValue)

        val actualUpdate = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = updateSqlBuilder.build(
                table.name, listOf(table.int.name), null, ARG_PLACEHOLDER
        )

        val expectedUpdate = UpdateImpl(expectedSql, listOf(overwritingValue))

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.tableName).isEqualTo(table.name)
        assertThat(statement.sql).isEqualTo(expectedSql)
        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(overwritingValue)
    }


    @Test
    fun testDefiningSetAndWhereClausesDoesNotFireAnUpdateCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        UpdateBuilderImpl(table, updateSqlBuilder, ARG_PLACEHOLDER).set(table.int, 5)
        assertThat(engine.compiledUpdates).isEmpty()
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        val updateSqlBuilder = bundle.updateSqlBuilder

        // build & compile
        val builder = UpdateBuilderImpl(table, updateSqlBuilder, ARG_PLACEHOLDER)
                .set(table.short, 5.toShort())
                .set(table.int, 6)
                .set(table.long, 7L)
                .set(table.float, 8F)
                .set(table.double, 9.toDouble())
                .set(table.string, "test 10")
                .set(table.blob, byteArrayOf(11))
                .set(table.nullableInt, null)

        val actualUpdate = builder.build()
        builder.compile()

        // build expected insert
        val expectedSql = updateSqlBuilder.build(
                table.name,
                listOf(
                        table.short.name, table.int.name, table.long.name, table.float.name,
                        table.double.name, table.string.name, table.blob.name,
                        table.nullableInt.name
                ), null, ARG_PLACEHOLDER
        )

        val expectedUpdate = UpdateImpl(
                expectedSql, listOf(5.toShort(), 6, 7L, 8F, 9.0, "test 10",
                byteArrayOf(11), TypedNull(Type.INT))
        )

        // Verify
        assertEquality(actualUpdate, expectedUpdate)
        assertThat(engine.compiledUpdates).hasSize(1)
        val statement: OneShotCompiledUpdateForTest = engine.compiledUpdates.first()
        assertThat(statement.tableName).isEqualTo(table.name)
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
            val updateSqlBuilder: UpdateSqlBuilder) {

        companion object {
            fun default(): Bundle {
                val engine = EngineForTestingBuilders.createWithOneShotStatements()
                val table = TableForTest(configuration = defaultTableConfiguration(engine))
                return Bundle(table, engine, UpdateSqlBuilderForTesting())
            }
        }
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"

        private fun assertEquality(actualUpdate: Update, expectedUpdate: Update) {
            assertThat(actualUpdate.sql).isEqualTo(expectedUpdate.sql)
            assertThat(actualUpdate.arguments).containsExactlyElementsOf(expectedUpdate.arguments)
        }
    }
}