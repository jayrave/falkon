package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.mapper.exceptions.MissingConverterException
import java.util.concurrent.ConcurrentHashMap

class TableConfigurationImpl(
        override val engine: Engine,
        override val nameFormatter: NameFormatter = CamelCaseToSnakeCaseFormatter()) :
        TableConfiguration {

    private val convertersForNonNullTypes: MutableMap<Class<*>, Converter<*>> = ConcurrentHashMap()
    private val convertersForNullableTypes: MutableMap<Class<*>, Converter<*>> = ConcurrentHashMap()

    override fun <R> getConverterForNullableType(clazz: Class<R>): Converter<R> {
        return getFrom(clazz, convertersForNullableTypes, true)
    }


    override fun <R : Any> getConverterForNonNullType(clazz: Class<R>): Converter<R> {
        return getFrom(clazz, convertersForNonNullTypes, false)
    }


    /**
     * Registers a [Converter] for the nullable form of [R] which can ge retrieved from
     * [getConverterForNullableType]
     *
     * CAUTION: Overwrites converters that were previously registered for the same form of [R]
     *
     * @param wrapForNonNullTypeIfRequired - If `true` & if the non-null form of [R] doesn't have
     * any registered converter, the passed in [converter] will be wrapped up in a
     * [NullableToNonNullConverter] & used for non-null form of [R]
     */
    fun <R> registerForNullableType(
            clazz: Class<R>, converter: Converter<R?>, wrapForNonNullTypeIfRequired: Boolean) {

        putIn(clazz, converter, convertersForNullableTypes)
        if (wrapForNonNullTypeIfRequired) {
            synchronized(convertersForNonNullTypes) {
                if (!convertersForNonNullTypes.contains(clazz)) {
                    putIn(clazz, NullableToNonNullConverter(converter), convertersForNonNullTypes)
                }
            }
        }
    }


    /**
     * Registers a [Converter] for the non-null form of [R] which can ge retrieved from
     * [getConverterForNonNullType]
     *
     * CAUTION: Overwrites converters that were previously registered for the same form of [R]
     */
    fun <R : Any> registerForNonNullType(clazz: Class<R>, converter: Converter<R>) {
        putIn(clazz, converter, convertersForNonNullTypes)
    }



    companion object {

        private fun putIn(
                clazz: Class<*>, converter: Converter<*>,
                to: MutableMap<Class<*>, Converter<*>>) {

            to[clazz] = converter
        }


        private fun <R> getFrom(
                clazz: Class<*>, from: Map<Class<*>, Converter<*>>,
                isNullableType: Boolean): Converter<R> {

            val converter = from[clazz]

            @Suppress("UNCHECKED_CAST")
            return when {
                converter != null -> converter as Converter<R>
                else -> {
                    val nullability = when (isNullableType) {
                        true -> "nullable"
                        else -> "non-null"
                    }

                    throw MissingConverterException(
                            "Converter not found for $nullability form of $clazz"
                    )
                }
            }
        }
    }
}