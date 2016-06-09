package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.*
import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Engine
import com.nhaarman.mockito_kotlin.mock

internal class ModelForTest(
        val short: Short = 0.toShort(),
        val int: Int = 0,
        val long: Long = 0L,
        val float: Float = 0F,
        val double: Double = 0.toDouble(),
        val string: String = "test",
        val blob: ByteArray = byteArrayOf(0),
        val nullable: Int? = null
)


internal class TableForTest(
        configuration: TableConfiguration = defaultTableConfiguration()) :
        BaseTable<ModelForTest, Int, Dao<ModelForTest, Int>>("test", configuration) {

    override val dao: Dao<ModelForTest, Int> = mock()
    override val idColumn: Column<ModelForTest, Int> = mock()
    override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

    val short = col(ModelForTest::short)
    val int = col(ModelForTest::int)
    val long = col(ModelForTest::long)
    val float = col(ModelForTest::float)
    val double = col(ModelForTest::double)
    val string = col(ModelForTest::string)
    val blob = col(ModelForTest::blob)
    val nullable = col(ModelForTest::nullable)
}


internal fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
    val configuration = TableConfigurationImpl(engine)
    configuration.registerDefaultConverters()
    return configuration
}