package com.jayrave.falkon.mapper

/**
 * Maps Kotlin types to & from SQL tables
 */
interface Table<T : Any, in ID : Any> {

    /**
     * Name of the table this class deals with
     */
    val name: String

    /**
     * Configuration for this table
     */
    val configuration: TableConfiguration

    /**
     * An immutable collection of all columns that belong to the table this class deals with.
     * Implementations must make sure that the backing [Collection] implementation has
     * deterministic iteration order
     */
    val allColumns: Collection<Column<T, *>>

    /**
     * An immutable collection of all columns that together represent the primary key
     * for the table this class deals with. Implementations must make sure that the
     * backing [Collection] implementation has deterministic iteration order
     */
    val idColumns: Collection<Column<T, *>>

    /**
     * An immutable collection of all columns that don't belong in the primary key
     * for the table this class deals with. Implementations must make sure that the
     * backing [Collection] implementation has deterministic iteration order
     */
    val nonIdColumns: Collection<Column<T, *>>

    /**
     * Value for [column] should be extracted from [id] if [column] belongs in the primary
     * key for the table this class deals with. Otherwise an exception must be thrown
     */
    fun <C> extractFrom(id: ID, column: Column<T, C>): C

    /**
     * @return A object representing a single row in the table this class deals with.
     * The values should be extracted out of the passed in [Value] instance
     */
    fun create(value: Value<T>): T
}
