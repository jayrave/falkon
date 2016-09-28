package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.exceptions.MissingConverterException
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

// TODO - how to test addColumn ?
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

        assertThat(tableForTest.allColumns).containsOnly(
                tableForTest.col1, tableForTest.col2, tableForTest.col3
        )
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

        // By default com.jayrave.falkon.mapper.SimplePropertyExtractor is used
        val model = ModelForTest(int = 5, nullableInt = null, nullableString = "hurray")
        assertThat(tableForTest.col1.extractPropertyFrom(model)).isEqualTo(model.int)
        assertThat(tableForTest.col2.extractPropertyFrom(model)).isEqualTo(model.nullableInt)
        assertThat(tableForTest.col3.extractPropertyFrom(model)).isEqualTo(model.nullableString)
    }


    @Test
    fun testConverterAcquisitionForNullableType() {
        val expectedUuid = UUID.randomUUID()
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNullableType(
                UUID::class.java, CustomUuidConverter(expectedUuid), false
        )

        val tableForTest = object : TableForTest(configuration) {
            val col1 = col(ModelForTest::nullableUuid)
        }

        // Assert & make sure that the converter we passed in is being used
        assertThat(tableForTest.col1.computePropertyFrom(mock())).isEqualTo(expectedUuid)
    }


    @Test
    fun testConverterAcquisitionForNonNullType() {
        val expectedUuid = UUID.randomUUID()
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNonNullType(
                UUID::class.java, NullableToNonNullConverter(CustomUuidConverter(expectedUuid))
        )

        val tableForTest = object : TableForTest(configuration) {
            val col1 = col(ModelForTest::uuid)
        }

        // Assert & make sure that the converter we passed in is being used
        assertThat(tableForTest.col1.computePropertyFrom(mock())).isEqualTo(expectedUuid)
    }


    @Test(expected = MissingConverterException::class)
    fun testConverterAcquisitionForUnregisteredNullableTypeThrows() {
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNonNullType(
                String::class.java, NullableToNonNullConverter(NullableStringConverter())
        )

        object : TableForTest(configuration) {
            init { col(ModelForTest::nullableString) }
        }
    }


    @Test(expected = MissingConverterException::class)
    fun testConverterAcquisitionForUnregisteredNonNullTypeThrows() {
        val configuration = TableConfigurationImpl(mock(), mock())
        configuration.registerForNullableType(String::class.java, NullableStringConverter(), false)

        object : TableForTest(configuration) {
            init { col(ModelForTest::string) }
        }
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

        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>): ModelForTest {
            throw UnsupportedOperationException()
        }


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