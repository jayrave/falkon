package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.TypeTranslator
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class TableConfigurationImpl(
        override val engine: Engine, override val typeTranslator: TypeTranslator,
        override val nameFormatter: NameFormatter = CamelCaseToSnakeCaseFormatter()) :
        TableConfiguration {

    private val convertersForNonNullTypes = ConcurrentHashMap<Type, Converter<*>?>()
    private val convertersForNullableTypes = ConcurrentHashMap<Type, Converter<*>?>()

    override fun <R> getConverterForNullableValuesOf(clazz: Class<R>): Converter<R>? {
        return getConverterForNullableValuesOf(clazz as Type)
    }


    override fun <R> getConverterForNullableValuesOf(type: Type): Converter<R>? {
        return convertersForNullableTypes.forType(type)
    }


    override fun <R : Any> getConverterForNonNullValuesOf(clazz: Class<R>): Converter<R>? {
        return getConverterForNonNullValuesOf(clazz as Type)
    }


    override fun <R : Any> getConverterForNonNullValuesOf(type: Type): Converter<R>? {
        return convertersForNonNullTypes.forType(type)
    }


    /**
     * Registers a [Converter] for the nullable form of [R]. Converters registered via this
     * method can be retrieved via both `getConverterForNullableValuesOf` for class & type
     *
     * *CAUTION:* Overwrites converters that were previously registered for the same form of [R]
     *
     * @param wrapForNonNullValuesIfRequired - If `true` & if the non-null form of [R]
     * doesn't have any registered converter, the passed in [converter] will be wrapped
     * up in a [NullableToNonNullConverter] & used for non-null form of [R]
     */
    fun <R> registerForNullableValues(
            clazz: Class<R>, converter: Converter<R?>,
            wrapForNonNullValuesIfRequired: Boolean) {

        registerForNullableValues(clazz as Type, converter, wrapForNonNullValuesIfRequired)
    }


    /**
     * A more flexible, less strict version of [registerForNullableValues] that takes
     * in a class. Prefer to use this only if [registerForNullableValues] doesn't cut it
     * as this method is not type-safe
     *
     * Converters registered via this method can be retrieved via both
     * `getConverterForNullableValuesOf` for class & type
     *
     * @see registerForNullableValues
     */
    fun <R> registerForNullableValues(
            type: Type, converter: Converter<R?>,
            wrapForNonNullValuesIfRequired: Boolean) {

        convertersForNullableTypes[type] = converter
        if (wrapForNonNullValuesIfRequired) {
            convertersForNonNullTypes.putIfAbsent(type, NullableToNonNullConverter(converter))
        }
    }


    /**
     * Registers a [Converter] for the non-null form of [R]. Converters registered via this
     * method can be retrieved via both `getConverterForNonNullValuesOf` for class & type
     *
     * *CAUTION:* Overwrites converters that were previously registered for the same form of [R]
     */
    fun <R : Any> registerForNonNullValues(clazz: Class<R>, converter: Converter<R>) {
        registerForNonNullValues(clazz as Type, converter)
    }


    /**
     * A more flexible, less strict version of [registerForNonNullValues] that takes
     * in a class. Prefer to use this only if [registerForNonNullValues] doesn't cut it
     * as this method is not type-safe.
     *
     * Converters registered via this method can be retrieved via both
     * `getConverterForNonNullValuesOf` for class & type
     */
    fun <R : Any> registerForNonNullValues(type: Type, converter: Converter<R>) {
        convertersForNonNullTypes[type] = converter
    }



    companion object {

        @Suppress("UNCHECKED_CAST")
        private fun <R> Map<Type, Converter<*>?>.forType(type: Type): Converter<R>? {
            return get(type) as Converter<R>?
        }
    }
}