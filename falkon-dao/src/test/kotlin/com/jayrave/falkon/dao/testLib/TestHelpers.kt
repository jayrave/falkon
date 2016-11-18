package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.mapper.*
import com.nhaarman.mockito_kotlin.mock

internal class ModelForTest(
        val short: Short = 0.toShort(),
        val int: Int = 0,
        val long: Long = 0L,
        val float: Float = 0F,
        val double: Double = 0.toDouble(),
        val string: String = "test",
        val blob: ByteArray = byteArrayOf(0),
        val nullableShort: Short? = null,
        val nullableInt: Int? = null,
        val nullableLong: Long? = null,
        val nullableFloat: Float? = null,
        val nullableDouble: Double? = null,
        val nullableString: String? = null,
        val nullableBlob: ByteArray? = null
)


internal class TableForTest(
        name: String = "test", configuration: TableConfiguration = defaultTableConfiguration()) :
        BaseTable<ModelForTest, Int>(name, configuration) {

    override fun <C> extractFrom(id: Int, column: Column<ModelForTest, C>) = throw exception()
    override fun create(value: Value<ModelForTest>) = throw exception()

    val short = col(ModelForTest::short)
    val int = col(ModelForTest::int)
    val long = col(ModelForTest::long)
    val float = col(ModelForTest::float)
    val double = col(ModelForTest::double)
    val string = col(ModelForTest::string)
    val blob = col(ModelForTest::blob)
    val nullableShort = col(ModelForTest::nullableShort)
    val nullableInt = col(ModelForTest::nullableInt)
    val nullableLong = col(ModelForTest::nullableLong)
    val nullableFloat = col(ModelForTest::nullableFloat)
    val nullableDouble = col(ModelForTest::nullableDouble)
    val nullableString = col(ModelForTest::nullableString)
    val nullableBlob = col(ModelForTest::nullableBlob)

    private fun exception() = UnsupportedOperationException()
}


internal fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
    val configuration = TableConfigurationImpl(engine, mock())
    configuration.registerDefaultConverters()
    return configuration
}