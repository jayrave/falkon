package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type

/**
 * [Column] defines how a property of a type corresponds to the column of a SQL table
 *      [T] => Table type
 *      [C] => Column type
 *
 * [Column] is an extension of [ReadOnlyColumnOfTable] that knows how to convert a property
 * to its storage form
 */
interface Column<T : Any, C> : ReadOnlyColumnOfTable<T, C> {

    /**
     * The [Type] this column will be stored as in the database
     */
    val dbType: Type

    /**
     * `true` if this column is (or part of) the primary key of the table it belongs to;
     * `false` otherwise
     */
    val isId: Boolean

    /**
     * To extract the property from the containing object
     */
    fun extractPropertyFrom(t: T): C

    /**
     * From property to the form it would get stored in the database
     *
     * [property] the property to compute the storage form for
     * @return - how the passed in [property] gets stored as
     */
    fun computeStorageFormOf(property: C): Any?

    /**
     * Storage form of [property] is computed and is sent to [DataConsumer]
     *
     * [property] the property to convert and store
     * @param dataConsumer consumer that consumes the storage form
     */
    fun putStorageFormIn(property: C, dataConsumer: DataConsumer)
}