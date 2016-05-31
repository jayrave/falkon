package com.jayrave.falkon.dao

import com.jayrave.falkon.*
import com.jayrave.falkon.engine.Sink
import com.nhaarman.mockito_kotlin.mock

private fun defaultConfiguration(): TableConfiguration<Sink> {
    val configuration = TableConfigurationImpl<Sink>(mock())
    configuration.registerDefaultConverters()
    return configuration
}


class ModelForTest(
        val id: Int = 0,
        val string: String = "test",
        val nullableInt: Int? = null,
        val nullableString: String? = null
)


class TableForTest(
        name: String = "test", configuration: TableConfiguration<Sink> = defaultConfiguration()) :
        BaseTable<ModelForTest, Int, Dao<ModelForTest, Int, Sink>, Sink>(name, configuration) {

    override val dao: Dao<ModelForTest, Int, Sink> by lazy { DaoImpl(this) }
    override val idColumn: Column<ModelForTest, Int> by lazy { id }
    override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

    val id = col(ModelForTest::id)
    val string = col(ModelForTest::string)
    val nullableInt = col(ModelForTest::nullableInt)
    val nullableString = col(ModelForTest::nullableString)
}