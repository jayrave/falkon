package com.jayrave.falkon

import com.jayrave.falkon.dao.Dao

interface Table<T : Any, ID : Any, D : Dao<T, ID>> {

    val name: String
    val configuration: TableConfiguration
    val dao: D
    val allColumns: Set<Column<T, *>>
    val idColumn: Column<T, ID>

    fun create(value: Value<T>): T
}
