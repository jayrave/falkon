package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.*
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeleteBuilderImplTest {

    private val table = TableForTest()
    private val engine = table.configuration.engine
    private val builder = DeleteBuilderImpl(table)

    @Test
    fun testDeleteWithoutWhere() {
        builder.delete()
        verify(engine).delete(eq(table.name), isNull(), isNull())
    }


    @Test
    fun testDeleteWithWhere() {
        builder.where().eq(table.int, 5)
        builder.delete()
        verify(engine).delete(eq(table.name), eq("int = ?"), eq(listOf(5)))
    }


    @Test
    fun testDeleteViaWhere() {
        builder.where().eq(table.int, 5).delete()
        verify(engine).delete(eq(table.name), eq("int = ?"), eq(listOf(5)))
    }


    @Test
    fun testWhereGetsOverwrittenOnRedefining() {
        builder.where().eq(table.int, 5)
        builder.where().eq(table.string, "test").delete()
        verify(engine).delete(eq(table.name), eq("string = ?"), eq(listOf("test")))
    }


    @Test
    fun testDefiningWhereClauseDoesNotFireADeleteCall() {
        builder.where().eq(table.int, 5)
        verifyZeroInteractions(engine)
    }


    @Test
    fun testDeleteReportsCorrectRowCount() {
        val numberOfRowsAffected = 112
        whenever(engine.delete(any(), any(), any())).thenReturn(numberOfRowsAffected)
        assertThat(builder.delete()).isEqualTo(numberOfRowsAffected)
    }


    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test"
    )



    private class TableForTest(
            configuration: TableConfiguration<Engine<Sink>, Sink> = defaultConfiguration()) :
            BaseTable<ModelForTest, Int, Engine<Sink>, Sink>("test", configuration) {

        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

        val int = col(ModelForTest::int)
        val string = col(ModelForTest::string)

        companion object {
            private fun defaultConfiguration(): TableConfiguration<Engine<Sink>, Sink> {
                val configuration = TableConfigurationImpl<Engine<Sink>, Sink>(mock())
                configuration.registerDefaultConverters()
                return configuration
            }
        }
    }
}