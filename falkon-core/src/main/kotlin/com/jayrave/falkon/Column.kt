package com.jayrave.falkon

/**
 * Column defines how a property of a type corresponds to the column of a SQL table
 *      [T] => Table type
 *      [C] => Column type
 */
interface Column<T : Any, C> {

    /**
     * Name of the SQL column
     */
    val name: String

    /**
     * True if this column is primary key or part of a composite primary key
     */
    val id: Boolean

    /**
     * A converter between the SQL type and `C`
     */
    val converter: Converter<C>

    /**
     * To get a non-null object for null in SQL land
     */
    val nullFromSqlSubstitute: NullSubstitute<C>

    /**
     * To write a non-null SQL value for null in kotlin land
     */
    val nullToSqlSubstitute: NullSubstitute<C>?

    /**
     * A function to extract the property from the containing object
     */
    val propertyExtractor: (T) -> C


    /**
     * @return - how the passed in [value] gets stored as
     */
    fun computeStorageFormOf(value: C): Any?
}