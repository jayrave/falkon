package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.exceptions.ConversionException

class NullableShortConverter : Converter<Short?> {
    override val dbType: Type = Type.SHORT
    override fun from(dataProducer: DataProducer): Short? = dataProducer.getShort()
    override fun to(value: Short?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableIntConverter : Converter<Int?> {
    override val dbType: Type = Type.INT
    override fun from(dataProducer: DataProducer): Int? = dataProducer.getInt()
    override fun to(value: Int?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableLongConverter : Converter<Long?> {
    override val dbType: Type = Type.LONG
    override fun from(dataProducer: DataProducer): Long? = dataProducer.getLong()
    override fun to(value: Long?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableFloatConverter : Converter<Float?> {
    override val dbType: Type = Type.FLOAT
    override fun from(dataProducer: DataProducer): Float? = dataProducer.getFloat()
    override fun to(value: Float?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableDoubleConverter : Converter<Double?> {
    override val dbType: Type = Type.DOUBLE
    override fun from(dataProducer: DataProducer): Double? = dataProducer.getDouble()
    override fun to(value: Double?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableStringConverter : Converter<String?> {
    override val dbType: Type = Type.STRING
    override fun from(dataProducer: DataProducer): String? = dataProducer.getString()
    override fun to(value: String?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


class NullableBlobConverter : Converter<ByteArray?> {
    override val dbType: Type = Type.BLOB
    override fun from(dataProducer: DataProducer): ByteArray? = dataProducer.getBlob()
    override fun to(value: ByteArray?, dataConsumer: DataConsumer) = dataConsumer.put(value)
}


/**
 * [Byte] is stored as [Short]. When getting data back from the database, an exception is
 * thrown if the retrieved short is outside of byte's limits
 */
class NullableByteConverter : Converter<Byte?> {

    override val dbType: Type = Type.SHORT

    override fun from(dataProducer: DataProducer): Byte? {
        val short = dataProducer.getShort()
        return when (short) {
            null -> null
            else -> when (short >= Byte.MIN_VALUE && short <= Byte.MAX_VALUE) {
                true -> short.toByte()
                else -> throw ConversionException(
                        "Stored value must be in [${Byte.MIN_VALUE}, " +
                                "${Byte.MAX_VALUE}], but is $short"
                )
            }
        }
    }

    override fun to(value: Byte?, dataConsumer: DataConsumer) = dataConsumer.put(value?.toShort())
}


/**
 * [Char] is stored as [String]. When getting data back from the database, an exception is
 * thrown if the retrieved string is empty or has more than one character
 */
class NullableCharConverter : Converter<Char?> {

    override val dbType: Type = Type.STRING

    override fun from(dataProducer: DataProducer): Char? {
        val string = dataProducer.getString()
        return when (string) {
            null -> null
            else -> when (string.length == 1) {
                true -> string.single()
                false -> throw ConversionException(
                        "Stored value must be a single char text, but is $string"
                )
            }
        }
    }

    override fun to(value: Char?, dataConsumer: DataConsumer) = dataConsumer.put(value?.toString())
}


/**
 * [Boolean] is either 0 or 1
 */
class NullableBooleanConverter : Converter<Boolean?> {

    override val dbType: Type = Type.SHORT

    override fun from(dataProducer: DataProducer): Boolean? {
        val short = dataProducer.getShort()
        return when (short) {
            null -> null
            else -> when (short) {
                TRUE_VALUE -> true
                FALSE_VALUE -> false
                else -> throw ConversionException(
                        "Stored value must be either $TRUE_VALUE or $FALSE_VALUE, but is $short"
                )
            }
        }
    }

    override fun to(value: Boolean?, dataConsumer: DataConsumer) {
        val short: Short? = when (value) {
            null -> null
            else -> when (value) {
                true -> TRUE_VALUE
                false -> FALSE_VALUE
            }
        }

        dataConsumer.put(short)
    }

    companion object {
        private const val TRUE_VALUE: Short = 1
        private const val FALSE_VALUE: Short = 0
    }
}


/**
 * Use this to wrap converters that work with nullable types to work as converters that work
 * with non-null types. It is assumed that if the [DataProducer] produces a non-null value,
 * the passed in delegate also produces a non-null value. If it is not the case, this converter
 * will thrown an exception when trying to extract values from a [DataProducer]
 */
class NullableToNonNullConverter<T>(private val delegate: Converter<T?>) : Converter<T> {
    override val dbType: Type = delegate.dbType
    override fun from(dataProducer: DataProducer): T  = delegate.from(dataProducer)!!
    override fun to(value: T, dataConsumer: DataConsumer) = delegate.to(value, dataConsumer)
}