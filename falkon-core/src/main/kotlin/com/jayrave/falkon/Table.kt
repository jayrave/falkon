package com.jayrave.falkon

interface Table<T : Any, ID : Any> {

    val name: String
    val configuration: TableConfiguration
    val allColumns: Set<Column<T, *>>
    val idColumn: Column<T, ID>

    fun create(value: Value<T>): T
}
