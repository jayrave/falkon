package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.TypeTranslator
import java.lang.reflect.Type

interface TableConfiguration {

    /**
     * The engine that will be used by the table this configuration object is passed to
     */
    val engine: Engine

    /**
     * To find the engine friendly versions of [Type]. Mostly used while building
     * CREATE TABLE SQL statements
     */
    val typeTranslator: TypeTranslator

    /**
     * The formatter that will be used to convert property names to column names
     */
    val nameFormatter: NameFormatter

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R> getConverterForNullableValuesOf(clazz: Class<R>): Converter<R>?

    /**
     * A more flexible, less strict version of [getConverterForNullableValuesOf] that takes
     * in a class. Prefer to use this only if [getConverterForNullableValuesOf] doesn't cut it
     * as this method is not type-safe
     */
    fun <R> getConverterForNullableValuesOf(type: Type): Converter<R>?

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R : Any> getConverterForNonNullValuesOf(clazz: Class<R>): Converter<R>?

    /**
     * A more flexible, less strict version of [getConverterForNonNullValuesOf] that takes
     * in a class. Prefer to use this only if [getConverterForNonNullValuesOf] doesn't cut it
     * as this method is not type-safe
     */
    fun <R : Any> getConverterForNonNullValuesOf(type: Type): Converter<R>?
}