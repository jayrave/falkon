package com.jayrave.falkon.mapper

interface Table<T : Any, ID : Any> {

    /**
     * Name of the table this class deals with
     */
    val name: String

    /**
     * Configuration for this table
     */
    val configuration: TableConfiguration

    /**
     * The column that represents the primary key for the table this class deals with
     */
    val idColumn: Column<T, ID>

    /**
     * A set of all columns that belong to the table this class deals with. Implementations
     * must make sure that the backing [Set] implementation is thread-safe
     */
    val allColumns: Set<Column<T, *>>

    /**
     * @return A object representing a single row in the table this class deals with.
     * The values should be extracted out of the passed in [Value] instance
     */
    fun create(value: Value<T>): T
}
