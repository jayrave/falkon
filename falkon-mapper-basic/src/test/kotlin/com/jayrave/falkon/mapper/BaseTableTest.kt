package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.exceptions.MissingConverterException
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class BaseTableTest {

    @Test
    fun `table without any columns`() {
        val tableForTest = object : TableForTest() {}
        assertThat(tableForTest.allColumns).isEmpty()
        assertThat(tableForTest.idColumns).isEmpty()
        assertThat(tableForTest.nonIdColumns).isEmpty()
    }


    @Test
    fun `table with one id column`() {
        val tableForTest = object : TableForTest() {
            val col = col(ModelForTest::int, isId = true)
        }

        assertThat(tableForTest.allColumns).containsOnly(tableForTest.col)
        assertThat(tableForTest.idColumns).containsOnly(tableForTest.col)
        assertThat(tableForTest.nonIdColumns).isEmpty()
    }


    @Test
    fun `table with one non id column`() {
        val tableForTest = object : TableForTest() { val col = col(ModelForTest::int) }

        assertThat(tableForTest.allColumns).containsOnly(tableForTest.col)
        assertThat(tableForTest.idColumns).isEmpty()
        assertThat(tableForTest.nonIdColumns).containsOnly(tableForTest.col)
    }


    @Test
    fun `table with multiple id & non id columns`() {
        val tableForTest = object : TableForTest() {
            val id1 = col(ModelForTest::int, isId = true)
            val nonId1 = col(ModelForTest::blob)
            val id2 = col(ModelForTest::nullableString, isId = true)
            val nonId2 = addColumn<String>("string", false, mock(), mock())
            val id3 = addColumn<String>("name", true, mock(), mock())
            val nonId3 = col(ModelForTest::nullableInt)
        }

        assertThat(tableForTest.allColumns).containsOnly(
                tableForTest.id1, tableForTest.id2, tableForTest.id3,
                tableForTest.nonId1, tableForTest.nonId2, tableForTest.nonId3
        )

        assertThat(tableForTest.idColumns).containsOnly(
                tableForTest.id1, tableForTest.id2, tableForTest.id3
        )

        assertThat(tableForTest.nonIdColumns).containsOnly(
                tableForTest.nonId1, tableForTest.nonId2, tableForTest.nonId3
        )
    }


    @Test
    fun `default name formatting`() {
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
    fun `default property extractor`() {
        val tableForTest = object : TableForTest() {
            val col1 = col(ModelForTest::int)
            val col2 = col(ModelForTest::nullableInt)
            val col3 = col(ModelForTest::nullableString)
        }

        // By default com.jayrave.falkon.mapper.SimplePropertyExtractor is used
        val model = ModelForTest(int = 5, nullableInt = null, nullableString = "hurray")
        assertThat(tableForTest.col1.extractPropertyFrom(model)).isEqualTo(model.int)
        assertThat(tableForTest.col2.extractPropertyFrom(model)).isEqualTo(model.nullableInt)
        assertThat(tableForTest.col3.extractPropertyFrom(model)).isEqualTo(model.nullableString)
    }


    @Test
    fun `acquisition of converter for nullable value`() {
        val expectedUuid = UUID.randomUUID()
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNullableValues(
                UUID::class.java, CustomUuidConverter(expectedUuid), false
        )

        val tableForTest = object : TableForTest(configuration) {
            val col1 = col(ModelForTest::nullableUuid)
        }

        // Assert & make sure that the converter we passed in is being used
        assertThat(tableForTest.col1.computePropertyFrom(mock())).isEqualTo(expectedUuid)
    }


    @Test
    fun `acquisition of converter for non-null value`() {
        val expectedUuid = UUID.randomUUID()
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNonNullValues(
                UUID::class.java, NullableToNonNullConverter(CustomUuidConverter(expectedUuid))
        )

        val tableForTest = object : TableForTest(configuration) {
            val col1 = col(ModelForTest::uuid)
        }

        // Assert & make sure that the converter we passed in is being used
        assertThat(tableForTest.col1.computePropertyFrom(mock())).isEqualTo(expectedUuid)
    }


    @Test(expected = MissingConverterException::class)
    fun `acquisition of converter for nullable value for unregistered class throws`() {
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNonNullValues(
                String::class.java, NullableToNonNullConverter(NullableStringConverter())
        )

        object : TableForTest(configuration) {
            init { col(ModelForTest::nullableString) }
        }
    }


    @Test(expected = MissingConverterException::class)
    fun `acquisition of converter for non-null value for unregistered class throws`() {
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNullableValues(
                String::class.java, NullableStringConverter(), false
        )

        object : TableForTest(configuration) {
            init { col(ModelForTest::string) }
        }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #col after accessing allColumns throws`() {
        `adding columns via #col too late throws` { it.allColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addColumn after accessing allColumns throws`() {
        `adding columns via #addColumn too late throws` { it.allColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #col after accessing idColumns throws`() {
        `adding columns via #col too late throws` { it.idColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addColumn after accessing idColumns throws`() {
        `adding columns via #addColumn too late throws` { it.idColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #col after accessing nonIdColumns throws`() {
        `adding columns via #col too late throws` { it.nonIdColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addColumn after accessing nonIdColumns throws`() {
        `adding columns via #addColumn too late throws` { it.nonIdColumns }
    }


    private fun `adding columns via #col too late throws`(
            latenessCausingOp: (TableForTest) -> Any?) {

        val tableForTest = object : TableForTest() {}
        latenessCausingOp.invoke(tableForTest)
        tableForTest.col(ModelForTest::string)
    }


    private fun `adding columns via #addColumn too late throws`(
            latenessCausingOp: (TableForTest) -> Any?) {

        val tableForTest = object : TableForTest() {}
        latenessCausingOp.invoke(tableForTest)
        tableForTest.addColumn<String>("string", false, mock(), mock())
    }



    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test non-null string",
            val blob: ByteArray = byteArrayOf(1),
            val nullableInt: Int? = 0,
            val nullableString: String? = "test nullable string",
            val thisIsACrazyNameForAColumn: Boolean = true,
            val uuid: UUID = UUID.randomUUID(),
            val nullableUuid: UUID? = UUID.randomUUID()
    )



    private abstract class TableForTest(
            configuration: TableConfiguration = defaultTableConfiguration()) :
            BaseTable<ModelForTest, Int>("test", configuration) {

        override fun <C> extractFrom(id: Int, column: Column<ModelForTest, C>) = throw exception()
        override fun create(value: Table.Value<ModelForTest>) = throw exception()
        private fun exception() = UnsupportedOperationException()

        companion object {
            private fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
                val configuration = TableConfigurationImpl(engine, mock())
                configuration.registerDefaultConverters()
                return configuration
            }
        }
    }



    companion object {
        private class CustomUuidConverter(private val defaultValue: UUID) : Converter<UUID?> {
            override val dbType: Type = Type.STRING
            override fun from(dataProducer: DataProducer): UUID? = defaultValue
            override fun to(value: UUID?, dataConsumer: DataConsumer) {
                throw UnsupportedOperationException("not implemented")
            }
        }
    }
}