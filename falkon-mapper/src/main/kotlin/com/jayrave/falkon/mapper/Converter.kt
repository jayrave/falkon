package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type

/**
 * Acts as a bridge between Kotlin & SQL types
 */
interface Converter<T> {

    /**
     * The [Type] this converter sends to [DataConsumer]
     */
    val dbType: Type

    /**
     * Produces [T] from the passed in [DataProducer]
     */
    fun from(dataProducer: DataProducer): T

    /**
     * Writes the passed in [T] into the passed in [DataConsumer]
     */
    fun to(value: T, dataConsumer: DataConsumer)
}