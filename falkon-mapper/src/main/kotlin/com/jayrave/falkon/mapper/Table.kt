package com.jayrave.falkon.mapper

/**
 * Maps Kotlin types to & from SQL tables
 */
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
     * A collection of all columns that belong to the table this class deals with. Implementations
     * must make sure that the backing [Collection] implementation is thread-safe & the iteration
     * order is the same as the insertion order
     */
    val allColumns: Collection<Column<T, *>>

    /**
     * @return A object representing a single row in the table this class deals with.
     * The values should be extracted out of the passed in [Value] instance
     */
    fun create(value: Value<T>): T
}
