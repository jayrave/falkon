package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.exceptions.ConversionException

class NullableShortConverter : Converter<Short?> {
    override val dbType: Type = Type.SHORT
    override fun to(value: Short?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): Short? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getShort()
        }
    }
}


class NullableIntConverter : Converter<Int?> {
    override val dbType: Type = Type.INT
    override fun to(value: Int?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): Int? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getInt()
        }
    }
}


class NullableLongConverter : Converter<Long?> {
    override val dbType: Type = Type.LONG
    override fun to(value: Long?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): Long? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getLong()
        }
    }
}


class NullableFloatConverter : Converter<Float?> {
    override val dbType: Type = Type.FLOAT
    override fun to(value: Float?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): Float? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getFloat()
        }
    }
}


class NullableDoubleConverter : Converter<Double?> {
    override val dbType: Type = Type.DOUBLE
    override fun to(value: Double?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): Double? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getDouble()
        }
    }
}


class NullableStringConverter : Converter<String?> {
    override val dbType: Type = Type.STRING
    override fun to(value: String?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): String? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getString()
        }
    }
}


class NullableBlobConverter : Converter<ByteArray?> {
    override val dbType: Type = Type.BLOB
    override fun to(value: ByteArray?, dataConsumer: DataConsumer) = dataConsumer.put(value)
    override fun from(dataProducer: DataProducer): ByteArray? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> dataProducer.getBlob()
        }
    }
}


/**
 * [Byte] is stored as [Short]. When getting data back from the database, an exception is
 * thrown if the retrieved short is outside of byte's limits
 */
class NullableByteConverter : Converter<Byte?> {
    override val dbType: Type = Type.SHORT
    override fun to(value: Byte?, dataConsumer: DataConsumer) = dataConsumer.put(value?.toShort())
    override fun from(dataProducer: DataProducer): Byte? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> {
                val short = dataProducer.getShort()
                when (short >= Byte.MIN_VALUE && short <= Byte.MAX_VALUE) {
                    true -> short.toByte()
                    else -> throw ConversionException(
                            "Stored value must be in [${Byte.MIN_VALUE}, " +
                                    "${Byte.MAX_VALUE}], but is $short"
                    )
                }
            }
        }
    }
}


/**
 * [Char] is stored as [String]. When getting data back from the database, an exception is
 * thrown if the retrieved string is empty or has more than one character
 */
class NullableCharConverter : Converter<Char?> {
    override val dbType: Type = Type.STRING
    override fun to(value: Char?, dataConsumer: DataConsumer) = dataConsumer.put(value?.toString())
    override fun from(dataProducer: DataProducer): Char? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> {
                val string = dataProducer.getString()
                when (string.length == 1) {
                    true -> string.single()
                    false -> throw ConversionException(
                            "Stored value must be a single char text, but is $string"
                    )
                }
            }
        }
    }
}


/**
 * [Boolean] is either 0 or 1
 */
class NullableBooleanConverter : Converter<Boolean?> {

    override val dbType: Type = Type.SHORT

    override fun from(dataProducer: DataProducer): Boolean? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> {
                val short = dataProducer.getShort()
                when (short) {
                    TRUE_VALUE -> true
                    FALSE_VALUE -> false
                    else -> throw ConversionException(
                            "Stored value must be either $TRUE_VALUE or $FALSE_VALUE, but is $short"
                    )
                }
            }
        }
    }

    override fun to(value: Boolean?, dataConsumer: DataConsumer) {
        val short: Short? = when (value) {
            null -> null
            true -> TRUE_VALUE
            false -> FALSE_VALUE
        }

        dataConsumer.put(short)
    }

    companion object {
        private const val TRUE_VALUE: Short = 1
        private const val FALSE_VALUE: Short = 0
    }
}


/**
 * Stores enum by its name
 */
class NullableEnumByNameConverter<T : Enum<T>>(private val clazz: Class<T>) : Converter<T?> {
    override val dbType: Type = Type.STRING
    override fun to(value: T?, dataConsumer: DataConsumer) = dataConsumer.put(value?.name)
    override fun from(dataProducer: DataProducer): T? {
        return try {
            when (dataProducer.isNull()) {
                true -> null
                else -> java.lang.Enum.valueOf(clazz, dataProducer.getString())
            }

        } catch (e: IllegalArgumentException) {
            throw ConversionException(e)
        }
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