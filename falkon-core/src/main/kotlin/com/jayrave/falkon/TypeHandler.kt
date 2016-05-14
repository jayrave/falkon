package com.jayrave.falkon

interface TypeHandler<T> {
    fun get(source: Source, columnIndex: Int): T
    fun put(obj: T, sink: Sink, columnName: String)
}