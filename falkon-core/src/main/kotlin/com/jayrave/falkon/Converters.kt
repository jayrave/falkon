package com.jayrave.falkon

class NullableByteConverter : Converter<Byte?> {
    override fun from(dataProducer: DataProducer): Byte? = dataProducer.getByte()
    override fun to(value: Byte?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableCharConverter : Converter<Char?> {
    override fun from(dataProducer: DataProducer): Char? = dataProducer.getChar()
    override fun to(value: Char?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableShortConverter : Converter<Short?> {
    override fun from(dataProducer: DataProducer): Short? = dataProducer.getShort()
    override fun to(value: Short?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableIntConverter : Converter<Int?> {
    override fun from(dataProducer: DataProducer): Int? = dataProducer.getInt()
    override fun to(value: Int?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableLongConverter : Converter<Long?> {
    override fun from(dataProducer: DataProducer): Long? = dataProducer.getLong()
    override fun to(value: Long?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableFloatConverter : Converter<Float?> {
    override fun from(dataProducer: DataProducer): Float? = dataProducer.getFloat()
    override fun to(value: Float?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableDoubleConverter : Converter<Double?> {
    override fun from(dataProducer: DataProducer): Double? = dataProducer.getDouble()
    override fun to(value: Double?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableBooleanConverter : Converter<Boolean?> {
    override fun from(dataProducer: DataProducer): Boolean? = dataProducer.getBoolean()
    override fun to(value: Boolean?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableStringConverter : Converter<String?> {
    override fun from(dataProducer: DataProducer): String? = dataProducer.getString()
    override fun to(value: String?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableBlobConverter : Converter<ByteArray?> {
    override fun from(dataProducer: DataProducer): ByteArray? = dataProducer.getBlob()
    override fun to(value: ByteArray?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


/**
 * Use this to wrap converters that work with nullable types to work as converters that work with non-null types.
 * It is assumed that if the [DataProducer] produces a non-null value, the passed in delegate also produces a
 * non-null value. If it is not the case, this converter will thrown an exception when trying to extract values
 * from a [DataProducer]
 */
class NullableToNonNullConverter<T>(private val delegate: Converter<T?>) : Converter<T> {
    override fun from(dataProducer: DataProducer): T  = delegate.from(dataProducer)!!
    override fun to(value: T, dataConsumer: DataConsumer) = delegate.to(value, dataConsumer)
}