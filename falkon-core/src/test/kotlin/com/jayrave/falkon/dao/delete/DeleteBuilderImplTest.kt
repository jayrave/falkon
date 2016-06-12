package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.OneShotCompiledDeleteForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeleteBuilderImplTest {

    @Test
    fun testDeleteWithoutWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine
        
        val builder = DeleteBuilderImpl(table)
        builder.build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyDeleteSql(
                table.name, null
        ))

        assertThat(statement.boundArgs).isEmpty()
    }


    @Test
    fun testDeleteWithWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5)
        builder.build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyDeleteSql(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int"))
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testDeleteViaWhere() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5).build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyDeleteSql(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int"))
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.intBoundAt(1)).isEqualTo(5)
    }


    @Test
    fun testWhereGetsOverwrittenOnRedefining() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5)
        builder.where().eq(table.string, "test").build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyDeleteSql(
                table.name, listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "string"))
        ))

        assertThat(statement.boundArgs).hasSize(1)
        assertThat(statement.stringBoundAt(1)).isEqualTo("test")
    }


    @Test
    fun testDefiningWhereClauseDoesNotFireADeleteCall() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

        val builder = DeleteBuilderImpl(table)
        builder.where().eq(table.int, 5)
        assertThat(engine.compiledDeletes).isEmpty()
    }


    @Test
    fun testAllTypesAreBoundCorrectly() {
        val bundle = Bundle.default()
        val table = bundle.table
        val engine = bundle.engine

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
                .build()

        // Verify interactions with compiled statement
        assertThat(engine.compiledDeletes).hasSize(1)
        val statement: OneShotCompiledDeleteForTest = engine.compiledDeletes.first()
        assertThat(statement.sql).isEqualTo(EngineForTestingBuilders.buildDummyDeleteSql(
                table.name,
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "short"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "long"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "float"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "double"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "string"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "blob"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable")
                )
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