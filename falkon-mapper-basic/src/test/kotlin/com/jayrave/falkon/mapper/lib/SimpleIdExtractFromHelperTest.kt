package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.mapper.*
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SimpleIdExtractFromHelperTest {

    @Test
    fun `extract from returns appropriate value`() {
        val table = ExampleModelTable()
        assertThat(SimpleIdExtractFromHelper(table.id).extractFrom(6, table.id)).isEqualTo(6)
        assertThat(SimpleIdExtractFromHelper(table.id).extractFrom(-42, table.id)).isEqualTo(-42)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `initializer throws if column is not an id column`() {
        SimpleIdExtractFromHelper(ExampleModelTable().col1)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `extract from throws if passed in column is not an id column`() {
        val table = ExampleModelTable()
        SimpleIdExtractFromHelper(table.id).extractFrom(5, table.col1)
    }



    private class ExampleModel(val id: Int, val col1: String)



    private class ExampleModelTable :
            BaseTable<ExampleModel, Int>("test", defaultTableConfiguration()) {

        private fun exception() = NotImplementedError()
        override fun create(value: Table.Value<ExampleModel>) = throw exception()
        override fun <C> extractFrom(id: Int, column: Column<ExampleModel, C>) = throw exception()

        val id = col(ExampleModel::id, isId = true)
        val col1 = col(ExampleModel::col1)
    }



    companion object {
        private fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
            val configuration = TableConfigurationImpl(engine, mock())
            configuration.registerDefaultConverters()
            return configuration
        }
    }
}