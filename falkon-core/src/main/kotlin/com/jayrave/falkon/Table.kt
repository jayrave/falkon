package com.jayrave.falkon

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Sink

interface Table<T : Any, ID : Any, D : Dao<T, ID, S>, S : Sink> {

    val name: String
    val configuration: TableConfiguration<S>
    val dao: D
    val allColumns: Set<Column<T, *>>
    val idColumn: Column<T, ID>

    fun create(value: Value<T>): T
}
