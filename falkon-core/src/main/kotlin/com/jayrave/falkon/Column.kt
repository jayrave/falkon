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
     * To extract the property from the containing object
     */
    val propertyExtractor: PropertyExtractor<T, C>

    /**
     * From property to the form it would get stored in the database
     *
     * @param property the property to compute the storage form for
     * @return - how the passed in [property] gets stored as
     */
    fun computeStorageFormOf(property: C): Any?

    /**
     * Storage form of [property] is computed and is sent to [DataConsumer]
     *
     * @param property the property to convert and store
     * @param dataConsumer consumer that consumes the storage form
     */
    fun putStorageFormIn(property: C, dataConsumer: DataConsumer)

    /**
     * From the form the data was stored in the database to property
     *
     * @param dataProducer the producer which supplies the stored data
     * @return - the property corresponding to the passed in stored form
     */
    fun computePropertyFrom(dataProducer: DataProducer): C
}