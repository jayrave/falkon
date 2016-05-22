package com.jayrave.falkon

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.reflect.KType

// TODO - how to test converter, nullFromSqlSubstitute & nullToSqlSubstitute ?
class BaseTableTest {

    @Test
    fun testAllColumnsWithZeroColumns() {
        val testTable = object : BaseTableForTest() {}
        assertThat(testTable.allColumns).isEmpty()
    }


    @Test
    fun testAllColumnsWithOneColumn() {
        val testTable = object : BaseTableForTest() {
            val col = col(ModelForTest::int)
        }

        assertThat(testTable.allColumns).containsOnly(testTable.col)
    }


    @Test
    fun testAllColumnsWithMultipleColumns() {
        val testTable = object : BaseTableForTest() {
            val col1 = col(ModelForTest::int)
            val col2 = col(ModelForTest::blob)
            val col3 = col(ModelForTest::nullableString)
        }

        assertThat(testTable.allColumns).containsOnly(testTable.col1, testTable.col2, testTable.col3)
    }


    @Test
    fun testDefaultNameFormatting() {
        val testTable = object : BaseTableForTest() {
            val col1 = col(ModelForTest::blob)
            val col2 = col(ModelForTest::nullableString)
            val col3 = col(ModelForTest::thisIsACrazyNameForAColumn)
        }

        // The table configuration we are passing in uses CamelCaseToSnakeCaseFormatter
        assertThat(testTable.col1.name).isEqualTo("blob")
        assertThat(testTable.col2.name).isEqualTo("nullable_string")
        assertThat(testTable.col3.name).isEqualTo("this_is_a_crazy_name_for_a_column")
    }


    @Test
    fun testDefaultPropertyExtractor() {
        val testTable = object : BaseTableForTest() {
            val col1 = col(ModelForTest::int)
            val col2 = col(ModelForTest::nullableInt)
            val col3 = col(ModelForTest::nullableString)
        }

        // Default property extract is a simple property getter
        val model = ModelForTest(int = 5, nullableInt = null, nullableString = "hurray")
        assertThat(testTable.col1.propertyExtractor.invoke(model)).isEqualTo(model.int)
        assertThat(testTable.col2.propertyExtractor.invoke(model)).isEqualTo(model.nullableInt)
        assertThat(testTable.col3.propertyExtractor.invoke(model)).isEqualTo(model.nullableString)
    }




    private class ModelForTest(
        val int: Int = 0,
        val blob: ByteArray = byteArrayOf(1),
        val nullableInt: Int? = 0,
        val nullableString: String? = "test",
        val thisIsACrazyNameForAColumn: Boolean = true
    )



    private class TableConfigurationForTest(
            override val engine: Engine<Sink> = mock(),
            override val nameFormatter: NameFormatter = CamelCaseToSnakeCaseFormatter()) :
            TableConfiguration<Engine<Sink>, Sink> {

        override fun <R> getConverter(type: KType) = mock<Converter<R>>()
    }



    private abstract class BaseTableForTest(
            configuration: TableConfiguration<Engine<Sink>, Sink> = TableConfigurationForTest()) :
            BaseTable<ModelForTest, Int, Engine<Sink>, Sink>("test", configuration) {

        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>): ModelForTest {
            throw UnsupportedOperationException()
        }
    }
}