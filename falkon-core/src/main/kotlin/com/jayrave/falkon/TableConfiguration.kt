package com.jayrave.falkon

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

interface TableConfiguration<E : Engine<S>, S : Sink> {

    /**
     * The engine that will be used by the table this configuration object is passed to
     */
    val engine: E

    /**
     * Implementations should return an appropriate converter or throw
     */
    fun <R> getConverter(clazz: Class<out R>): Converter<R>
}