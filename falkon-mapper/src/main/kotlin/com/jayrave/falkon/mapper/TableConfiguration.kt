package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Engine

interface TableConfiguration {

    /**
     * The engine that will be used by the table this configuration object is passed to
     */
    val engine: Engine

    /**
     * The formatter that will be used to convert property names to column names
     */
    val nameFormatter: NameFormatter

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R> getConverterForNullableType(clazz: Class<R>): Converter<R>

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R : Any> getConverterForNonNullType(clazz: Class<R>): Converter<R>
}