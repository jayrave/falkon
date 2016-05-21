package com.jayrave.falkon

/**
 * [converter] A converter between the SQL type and `C`
 * [nullFromSqlSubstitute] To get a non-null object for null in SQL land
 * [nullToSqlSubstitute] To write a non-null SQL value for null in kotlin land
 */
internal class ColumnImpl<T : Any, C>(
        override val name: String, override val propertyExtractor: (T) -> C, private val converter: Converter<C>,
        private val nullFromSqlSubstitute: NullSubstitute<C>, private val nullToSqlSubstitute: NullSubstitute<C>) :
        Column<T, C> {

    override fun computeStorageFormOf(property: C): Any? {
        // Perform null substitution if required
        val nullSubstitutedValue = when (property) {
            null -> nullToSqlSubstitute.value()
            else -> property
        }

        val valueHoldingDataConsumer = threadLocalValueHoldingDataConsumer.get()
        converter.to(nullSubstitutedValue, valueHoldingDataConsumer)
        return valueHoldingDataConsumer.mostRecentConsumedValue
    }

    override fun computePropertyFrom(dataProducer: DataProducer): C {
        return when (dataProducer.isNull()) {
            true -> nullFromSqlSubstitute.value() // Perform null substitution if required
            else -> converter.from(dataProducer)
        }
    }


    companion object {
        private val threadLocalValueHoldingDataConsumer = object : ThreadLocal<ValueHoldingDataConsumer>() {
            override fun initialValue(): ValueHoldingDataConsumer? {
                return ValueHoldingDataConsumer()
            }
        }
    }
}