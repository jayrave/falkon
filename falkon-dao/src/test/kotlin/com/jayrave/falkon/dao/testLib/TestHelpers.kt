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
        val flagPair: FlagPair = FlagPair(true, false),
        val nullableShort: Short? = null,
        val nullableInt: Int? = null,
        val nullableLong: Long? = null,
        val nullableFloat: Float? = null,
        val nullableDouble: Double? = null,
        val nullableString: String? = null,
        val nullableBlob: ByteArray? = null,
        val nullableFlagPair: FlagPair? = null
)


internal class TableForTest(
        name: String = "test", configuration: TableConfiguration = defaultTableConfiguration()) :
        BaseTable<ModelForTest, Int>(name, configuration) {

    override fun <C> extractFrom(id: Int, column: Column<ModelForTest, C>) = throw exception()
    override fun create(value: Table.Value<ModelForTest>) = throw exception()

    val short = col(ModelForTest::short, isId = true)
    val int = col(ModelForTest::int, isId = true)
    val long = col(ModelForTest::long)
    val float = col(ModelForTest::float)
    val double = col(ModelForTest::double)
    val string = col(ModelForTest::string, isId = true)
    val blob = col(ModelForTest::blob)
    val flagPair = col(ModelForTest::flagPair)
    val nullableShort = col(ModelForTest::nullableShort)
    val nullableInt = col(ModelForTest::nullableInt)
    val nullableLong = col(ModelForTest::nullableLong)
    val nullableFloat = col(ModelForTest::nullableFloat, isId = true)
    val nullableDouble = col(ModelForTest::nullableDouble)
    val nullableString = col(ModelForTest::nullableString)
    val nullableBlob = col(ModelForTest::nullableBlob)
    val nullableFlagPair = col(ModelForTest::nullableFlagPair)

    private fun exception() = UnsupportedOperationException()
}


internal fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
    val configuration = TableConfigurationImpl(engine, mock())
    configuration.registerDefaultConverters()
    configuration.registerForNullableValues(
            FlagPair::class.java, NullableFlagPairConverter(),
            wrapForNonNullValuesIfRequired = true
    )

    return configuration
}