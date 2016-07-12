package com.jayrave.falkon.dao

import com.jayrave.falkon.mapper.Column

/**
 * Extracts the property for the column from the passed in instance ([t])
 */
internal fun <T : Any, C> Column<T, C>.extractPropertyFrom(t: T): C {
    return propertyExtractor.extract(t)
}