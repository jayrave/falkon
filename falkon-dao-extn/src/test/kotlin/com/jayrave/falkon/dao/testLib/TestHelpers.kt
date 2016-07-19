package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.*
import com.jayrave.falkon.sqlBuilders.SimpleDeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.SimpleInsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.SimpleQuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.SimpleUpdateSqlBuilder
import com.nhaarman.mockito_kotlin.mock
import java.util.*

internal class ModelForTest(
        val id: UUID = UUID.randomUUID(),
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
        configuration: TableConfiguration = defaultTableConfiguration()) :
        BaseTable<ModelForTest, UUID>("test", configuration) {

    val dao: Dao<ModelForTest, UUID> = DaoImpl(
            this, "?", SimpleInsertSqlBuilder(), SimpleUpdateSqlBuilder(),
            SimpleDeleteSqlBuilder(), SimpleQuerySqlBuilder()
    )

    override val idColumn: Column<ModelForTest, UUID> get() = id
    override fun create(value: Value<ModelForTest>): ModelForTest {
        return ModelForTest(
                value of id, value of short, value of int, value of long, value of float,
                value of double, value of string, value of blob, value of nullableShort,
                value of nullableInt, value of nullableLong, value of nullableFloat,
                value of nullableDouble, value of nullableString, value of nullableBlob
        )
    }

    val id = col(ModelForTest::id)
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
}


internal fun defaultTableConfiguration(engine: Engine = mock()): TableConfiguration {
    val configuration = TableConfigurationImpl(engine, mock())
    configuration.registerDefaultConverters()
    configuration.registerUuidConverters()
    return configuration
}


internal fun TableConfigurationImpl.registerUuidConverters() {
    registerForNullableType(UUID::class.java, NullableUuidConverter(), true)
}


internal fun ModelForTest.testEquality(modelForTest: ModelForTest): Boolean {
    return id == modelForTest.id &&
            short == modelForTest.short &&
            int == modelForTest.int &&
            long == modelForTest.long &&
            float == modelForTest.float &&
            double == modelForTest.double &&
            string == modelForTest.string &&
            Arrays.equals(blob, modelForTest.blob) &&
            nullableShort == modelForTest.nullableShort &&
            nullableInt == modelForTest.nullableInt &&
            nullableLong == modelForTest.nullableLong &&
            nullableFloat == modelForTest.nullableFloat &&
            nullableDouble == modelForTest.nullableDouble &&
            nullableString == modelForTest.nullableString &&
            Arrays.equals(nullableBlob, modelForTest.nullableBlob)
}


private class NullableUuidConverter : Converter<UUID?> {

    override val dbType: Type = Type.STRING

    override fun from(dataProducer: DataProducer): UUID? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> UUID.fromString(dataProducer.getString())
        }
    }

    override fun to(value: UUID?, dataConsumer: DataConsumer) {
        dataConsumer.put(value?.toString())
    }
}