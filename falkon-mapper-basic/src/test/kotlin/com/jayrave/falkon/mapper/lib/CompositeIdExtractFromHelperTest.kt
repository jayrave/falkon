package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.mapper.*
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.*

import org.junit.Test

class CompositeIdExtractFromHelperTest {

    @Test
    fun `extract from returns appropriate value`() {
        val table = ExampleModelTable()
        val helper = CompositeIdExtractFromHelper
                .Builder<ExampleModelTable.Id>()
                .add(table.id1, ExampleModelTable.Id::id1)
                .add(table.id2, ExampleModelTable.Id::id2)
                .add(table.id3, ExampleModelTable.Id::id3)
                .build()

        val id = ExampleModelTable.Id(1234, 5678.0, "boaty mc boat face")
        assertThat(helper.extractFrom(id, table.id1)).isEqualTo(1234)
        assertThat(helper.extractFrom(id, table.id2)).isEqualTo(5678.0)
        assertThat(helper.extractFrom(id, table.id3)).isEqualTo("boaty mc boat face")
    }


    @Test(expected = IllegalArgumentException::class)
    fun `initializer throws if column is not an id column`() {
        CompositeIdExtractFromHelper
                .Builder<ExampleModelTable.Id>()
                .add(ExampleModelTable().col2, ExampleModelTable.Id::id3)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `extract from throws if passed in column is not an id column`() {
        val table = ExampleModelTable()
        CompositeIdExtractFromHelper
                .Builder<ExampleModelTable.Id>()
                .add(table.id1, ExampleModelTable.Id::id1)
                .build()
                .extractFrom(ExampleModelTable.Id(), table.col1)
    }



    private class ExampleModel(
            val id1: Int, val id2: Double, val id3: String, val col1: Float, val col2: String
    )



    private class ExampleModelTable :
            BaseTable<ExampleModel, Int>("test", defaultTableConfiguration()) {

        class Id(val id1: Int = 42, val id2: Double = 44.0, val id3: String = "test 46")

        private fun exception() = NotImplementedError()
        override fun create(value: Table.Value<ExampleModel>) = throw exception()
        override fun <C> extractFrom(id: Int, column: Column<ExampleModel, C>) = throw exception()

        val id1 = col(ExampleModel::id1, isId = true)
        val id2 = col(ExampleModel::id2, isId = true)
        val id3 = col(ExampleModel::id3, isId = true)
        val col1 = col(ExampleModel::col1)
        val col2 = col(ExampleModel::col2)
    }



    companion object {
        private fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
            val configuration = TableConfigurationImpl(engine, mock())
            configuration.registerDefaultConverters()
            return configuration
        }
    }
}