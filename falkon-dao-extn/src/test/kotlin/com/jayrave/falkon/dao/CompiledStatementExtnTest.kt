package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.*
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*
import com.jayrave.falkon.engine.CompiledStatement as CS

class CompiledStatementExtnTest {

    @Test
    fun `#bindColumns starts binding from index 1 by default`() {
        val table = TableForTest()
        val model = ModelForTest()

        val cs = CompiledStatementForTest()
        cs.bindColumns(table.allColumns, model)

        assertThat(cs.boundArgs.size).isEqualTo(2)
        assertThat(cs.boundArgs.first().index).isEqualTo(1)
        assertThat(cs.boundArgs.second().index).isEqualTo(2)
    }


    @Test
    fun `#bindColumns with value extractor starts binding from index 1 by default`() {
        val table = TableForTest()
        val model = ModelForTest()

        val cs = CompiledStatementForTest()
        cs.bindColumns(table.allColumns, model) { item, column ->
            column.extractPropertyFrom(item)
        }

        assertThat(cs.boundArgs.size).isEqualTo(2)
        assertThat(cs.boundArgs.first().index).isEqualTo(1)
        assertThat(cs.boundArgs.second().index).isEqualTo(2)
    }


    @Test
    fun `#bindColumns can bind from arbitrary index`() {
        val table = TableForTest()
        val model = ModelForTest()

        val cs = CompiledStatementForTest()
        cs.bindColumns(table.allColumns, model, startIndex = 5)

        assertThat(cs.boundArgs.size).isEqualTo(2)
        assertThat(cs.boundArgs.first().index).isEqualTo(5)
        assertThat(cs.boundArgs.second().index).isEqualTo(6)
    }


    @Test
    fun `#bindColumns with value extractor can bind from arbitrary index`() {
        val table = TableForTest()
        val model = ModelForTest()

        val cs = CompiledStatementForTest()
        cs.bindColumns(table.allColumns, model, startIndex = 5) { item, column ->
            column.extractPropertyFrom(item)
        }

        assertThat(cs.boundArgs.size).isEqualTo(2)
        assertThat(cs.boundArgs.first().index).isEqualTo(5)
        assertThat(cs.boundArgs.second().index).isEqualTo(6)
    }


    @Test
    fun `#bindColumns binds storage form`() {
        val table = TableForTest()
        val model = ModelForTest()

        val cs = CompiledStatementForTest()
        cs.bindColumns(table.allColumns, model)

        assertThat(cs.boundArgs.size).isEqualTo(2)
        assertThat(cs.boundArgs.first().value).isEqualTo(IntToStringConverter.asString(model.int))
        assertThat(cs.boundArgs.second().value).isEqualTo(IntToStringConverter.dbType)
    }


    @Test
    fun `#bindColumns with value extractor binds storage form`() {
        val table = TableForTest()
        val model = ModelForTest()

        val cs = CompiledStatementForTest()
        cs.bindColumns(table.allColumns, model) { item, column ->
            column.extractPropertyFrom(item)
        }

        assertThat(cs.boundArgs.size).isEqualTo(2)
        assertThat(cs.boundArgs.first().value).isEqualTo(IntToStringConverter.asString(model.int))
        assertThat(cs.boundArgs.second().value).isEqualTo(IntToStringConverter.dbType)
    }



    private class ModelForTest(
            val int: Int = 0,
            val nullableInt: Int? = null
    )



    @Suppress("unused")
    private class TableForTest(
            name: String = "test",
            config: TableConfiguration = TableConfigurationImpl(mock(), mock())) :
            BaseTable<ModelForTest, Int>(name, config) {

        val int = col(
                ModelForTest::int, isId = true,
                converter = NullableToNonNullConverter(IntToStringConverter())
        )

        val nullableInt = col(ModelForTest::nullableInt, converter = IntToStringConverter())
        override fun <C> extractFrom(id: Int, column: Column<ModelForTest, C>) = throw exception()
        override fun create(value: Table.Value<ModelForTest>) = throw exception()
    }



    private data class Arg(val index: Int, val value: Any)
    private class CompiledStatementForTest : CS<Int> {

        override val sql: String get() = throw exception()
        override val isClosed: Boolean get() = throw exception()
        private val _boundArgs = LinkedList<Arg>()
        val boundArgs: List<Arg> get() = _boundArgs

        override fun execute(): Int = throw exception()
        override fun bindShort(index: Int, value: Short): CS<Int> = throw exception()
        override fun bindInt(index: Int, value: Int): CS<Int> = throw exception()
        override fun bindLong(index: Int, value: Long): CS<Int> = throw exception()
        override fun bindFloat(index: Int, value: Float): CS<Int> = throw exception()
        override fun bindDouble(index: Int, value: Double): CS<Int> = throw exception()
        override fun bindBlob(index: Int, value: ByteArray): CS<Int> = throw exception()
        override fun close() = throw exception()
        override fun clearBindings(): CS<Int> = throw exception()

        override fun bindString(index: Int, value: String): CS<Int> {
            _boundArgs.add(Arg(index, value))
            return this
        }

        override fun bindNull(index: Int, type: Type): CS<Int> {
            _boundArgs.add(Arg(index, type))
            return this
        }
    }



    private class IntToStringConverter : Converter<Int?> {
        override val dbType: Type get() = Companion.dbType
        override fun from(dataProducer: DataProducer): Int? = throw exception()
        override fun to(value: Int?, dataConsumer: DataConsumer) {
            when (value) {
                null -> dataConsumer.put(null as String?)
                else -> dataConsumer.put(asString(value))
            }
        }


        companion object {
            val dbType: Type = Type.STRING
            fun asString(value: Int): String = "ha ha he he: $value"
        }
    }



    companion object {
        private fun exception() = UnsupportedOperationException()
        private fun <T> List<T>.second(): T = this[1]
    }
}