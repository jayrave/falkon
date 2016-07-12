package com.jayrave.falkon.mapper

/**
 * Extracts the property value based on the passed in [Column]
 */
interface Value<T : Any> {
    infix fun <C> of(column: Column<T, C>): C
}