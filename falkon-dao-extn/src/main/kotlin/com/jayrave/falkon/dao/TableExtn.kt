package com.jayrave.falkon.dao

import com.jayrave.falkon.mapper.Table

/**
 * Extracts the ID for the table from the passed in model ([t])
 */
internal fun <T : Any, ID : Any> Table<T, ID>.extractIdFrom(t: T): ID {
    return idColumn.extractPropertyFrom(t)
}