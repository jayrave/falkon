package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.mapper.Column

/**
 * Gives the qualified name of a column. It is of the format "table_name.column_name"
 */
internal val Column<*, *>.qualifiedName: String
    get() = "${table.name}.$name"