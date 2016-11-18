package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type

/**
 * [converter] A converter between the SQL type and [C]
 */
class ColumnImpl<T : Any, C>(
        override val table: Table<T, *>, override val name: String, override val isId: Boolean,
        private val propertyExtractor: PropertyExtractor<T, C>,
        private val converter: Converter<C>) : Column<T, C> {

    override val dbType: Type = converter.dbType

    override fun extractPropertyFrom(t: T): C {
        return propertyExtractor.extractFrom(t)
    }

    override fun computeStorageFormOf(property: C): Any? {
        val valueHoldingDataConsumer = threadLocalValueHoldingDataConsumer.get()
        putStorageFormIn(property, valueHoldingDataConsumer)
        return valueHoldingDataConsumer.mostRecentConsumedValue
    }

    override fun putStorageFormIn(property: C, dataConsumer: DataConsumer) {
        converter.to(property, dataConsumer)
    }

    override fun computePropertyFrom(dataProducer: DataProducer): C {
        return converter.from(dataProducer)
    }


    companion object {
        private val threadLocalValueHoldingDataConsumer =
                object : ThreadLocal<ValueHoldingDataConsumer>() {
                    override fun initialValue(): ValueHoldingDataConsumer? {
                        return ValueHoldingDataConsumer()
                    }
                }
    }
}