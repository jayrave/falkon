package com.jayrave.falkon

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

// TODO - how to test converter, nullFromSqlSubstitute, nullToSqlSubstitute, #addColumn & other stuff in BaseTable ?
class BaseTableTest {

    @Test
    fun testAllColumnsWithZeroColumns() {
        val tableForTest = object : TableForTest() {}
        assertThat(tableForTest.allColumns).isEmpty()
    }


    @Test
    fun testAllColumnsWithOneColumn() {
        val tableForTest1 = object : TableForTest() { val col = col(ModelForTest::int) }
        val tableForTest2 = object : TableForTest() { val col = col(ModelForTest::nullableInt) }

        assertThat(tableForTest1.allColumns).containsOnly(tableForTest1.col)
        assertThat(tableForTest2.allColumns).containsOnly(tableForTest2.col)
    }


    @Test
    fun testAllColumnsWithMultipleColumns() {
        val tableForTest = object : TableForTest() {
            val col1 = col(ModelForTest::int)
            val col2 = col(ModelForTest::blob)
            val col3 = col(ModelForTest::nullableString)
        }

        assertThat(tableForTest.allColumns).containsOnly(tableForTest.col1, tableForTest.col2, tableForTest.col3)
    }


    @Test
    fun testDefaultNameFormatting() {
        val tableForTest = object : TableForTest() {
            val col1 = col(ModelForTest::blob)
            val col2 = col(ModelForTest::nullableString)
            val col3 = col(ModelForTest::thisIsACrazyNameForAColumn)
        }

        // The table configuration we are passing in uses CamelCaseToSnakeCaseFormatter
        assertThat(tableForTest.col1.name).isEqualTo("blob")
        assertThat(tableForTest.col2.name).isEqualTo("nullable_string")
        assertThat(tableForTest.col3.name).isEqualTo("this_is_a_crazy_name_for_a_column")
    }


    @Test
    fun testDefaultPropertyExtractor() {
        val tableForTest = object : TableForTest() {
            val col1 = col(ModelForTest::int)
            val col2 = col(ModelForTest::nullableInt)
            val col3 = col(ModelForTest::nullableString)
        }

        // By default com.jayrave.falkon.SimplePropertyExtractor is used
        val model = ModelForTest(int = 5, nullableInt = null, nullableString = "hurray")
        assertThat(tableForTest.col1.propertyExtractor.extract(model)).isEqualTo(model.int)
        assertThat(tableForTest.col2.propertyExtractor.extract(model)).isEqualTo(model.nullableInt)
        assertThat(tableForTest.col3.propertyExtractor.extract(model)).isEqualTo(model.nullableString)
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
            TableConfiguration<Sink> {

        override fun <R> getConverterForNullableType(clazz: Class<R>) = mock<Converter<R>>()
        override fun <R : Any> getConverterForNonNullType(clazz: Class<R>) = mock<Converter<R>>()
    }



    private abstract class TableForTest(
            configuration: TableConfiguration<Sink> = TableConfigurationForTest()) :
            BaseTable<ModelForTest, Int, Dao<ModelForTest, Int, Sink>, Sink>("test", configuration) {

        override val dao: Dao<ModelForTest, Int, Sink> = mock()
        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>): ModelForTest {
            throw UnsupportedOperationException()
        }
    }
}