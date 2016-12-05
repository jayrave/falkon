package com.jayrave.falkon.mapper

/**
 * [ReadOnlyColumn] can only be read from, as the name signifies ;). It may belong to a table
 * or just be in the result set of a query (calculated on the fly)
 */
interface ReadOnlyColumn<out C> {

    /**
     * Name of the SQL column
     */
    val name: String

    /**
     * From the form the data is in the result set to property
     *
     * @param [dataProducer] the producer which supplies the result set data
     * @return property corresponding to the passed in data
     */
    fun computePropertyFrom(dataProducer: DataProducer): C
}