package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type

/**
 * [converter] A converter between the SQL type and `C`
 * [nullFromSqlSubstitute] To get a non-null object for null in SQL land
 * [nullToSqlSubstitute] To write a non-null SQL value for null in kotlin land
 */
class ColumnImpl<in T : Any, C>(
        override val name: String, override val propertyExtractor: PropertyExtractor<T, C>,
        private val converter: Converter<C>, private val nullFromSqlSubstitute: NullSubstitute<C>,
        private val nullToSqlSubstitute: NullSubstitute<C>) : Column<T, C> {

    override val dbType: Type = converter.dbType

    override fun computeStorageFormOf(property: C): Any? {
        val valueHoldingDataConsumer = threadLocalValueHoldingDataConsumer.get()
        putStorageFormIn(property, valueHoldingDataConsumer)
        return valueHoldingDataConsumer.mostRecentConsumedValue
    }

    override fun putStorageFormIn(property: C, dataConsumer: DataConsumer) {
        // Perform null substitution if required
        val nullSubstitutedValue = when (property) {
            null -> nullToSqlSubstitute.value()
            else -> property
        }

        converter.to(nullSubstitutedValue, dataConsumer)
    }

    override fun computePropertyFrom(dataProducer: DataProducer): C {
        return when (dataProducer.isNull()) {
            true -> nullFromSqlSubstitute.value() // Perform null substitution if required
            else -> converter.from(dataProducer)
        }
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