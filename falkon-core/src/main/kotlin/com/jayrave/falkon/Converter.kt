package com.jayrave.falkon

interface Converter<T> {
    fun from(dataProducer: NonNullDataProducer): T
    fun to(obj: T, dataConsumer: NullableDataConsumer)
}