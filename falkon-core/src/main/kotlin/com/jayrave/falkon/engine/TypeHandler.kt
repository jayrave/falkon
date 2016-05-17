package com.jayrave.falkon.engine

interface TypeHandler<T> {
    fun get(source: Source, columnIndex: Int): T
    fun put(obj: T, sink: Sink, columnName: String)
}