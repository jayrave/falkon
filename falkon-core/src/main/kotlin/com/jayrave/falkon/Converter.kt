package com.jayrave.falkon

interface Converter<T> {
    fun from(dataProducer: DataProducer): T
    fun to(value: T, dataConsumer: DataConsumer)
}