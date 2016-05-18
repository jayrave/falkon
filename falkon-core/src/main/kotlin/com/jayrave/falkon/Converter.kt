package com.jayrave.falkon

interface Converter<T> {
    fun from(dataProducer: NullableDataProducer): T
    fun to(obj: T, dataConsumer: NullableDataConsumer)
}