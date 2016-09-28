package com.jayrave.falkon.mapper

/**
 * Gives the value for the passed in [Column]
 */
interface Value<T : Any> {
    infix fun <C> of(column: Column<T, C>): C
}