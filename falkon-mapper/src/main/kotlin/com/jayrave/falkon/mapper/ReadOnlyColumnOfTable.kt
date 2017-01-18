package com.jayrave.falkon.mapper

/**
 * A stricter version of [ReadOnlyColumn] that always belongs to a table
 *      [T] => Table type
 *      [C] => Column type
 */
interface ReadOnlyColumnOfTable<T : Any, out C> : ReadOnlyColumn<C> {

    /**
     * Table this column belongs to
     */
    val table: Table<T, *>
}