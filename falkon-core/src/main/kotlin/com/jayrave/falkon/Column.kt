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
     * A function to extract the property from the containing object
     */
    val propertyExtractor: (T) -> C

    /**
     * From property to the form it would get stored in the database
     *
     * [property] the property to compute the storage form for
     *
     * @return - how the passed in [property] gets stored as
     */
    fun computeStorageFormOf(property: C): Any?

    /**
     * From the form the data was stored in the database to property
     *
     * [dataProducer] the producer which supplies the stored data
     *
     * @return - the property corresponding to the passed in stored form
     */
    fun computePropertyFrom(dataProducer: DataProducer): C
}