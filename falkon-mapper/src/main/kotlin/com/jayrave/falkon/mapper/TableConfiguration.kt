package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.TypeTranslator

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
    fun <R> getConverterForNullableType(clazz: Class<R>): Converter<R>?

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R : Any> getConverterForNonNullType(clazz: Class<R>): Converter<R>?
}