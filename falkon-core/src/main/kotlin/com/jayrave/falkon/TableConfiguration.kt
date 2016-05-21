package com.jayrave.falkon

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink
import kotlin.reflect.KType

interface TableConfiguration<E : Engine<S>, S : Sink> {

    /**
     * The engine that will be used by the table this configuration object is passed to
     */
    val engine: E

    /**
     * The formatter that will be used to convert property names to column names
     */
    val nameFormatter: NameFormatter

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R> getConverter(type: KType): Converter<R>
}