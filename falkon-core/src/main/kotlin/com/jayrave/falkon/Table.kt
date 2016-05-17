package com.jayrave.falkon

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

interface Table<T : Any, ID : Any, E : Engine<S>, S : Sink> {

    val name: String
    val configuration: TableConfiguration<E, S>
    val allColumns: Set<Column<T, *>>
    val idColumn: Column<T, ID>

    fun create(value: Value<T>): T
}
