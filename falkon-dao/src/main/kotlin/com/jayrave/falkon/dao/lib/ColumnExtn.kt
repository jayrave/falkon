package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.mapper.Column

/**
 * Gives the qualified name of a column. It is of the format "table_name.column_name".
 * This form is NOT safe to use as column's alias in queries as some db engine
 * don't like this (eg., Android's built-in SQLite via SQLiteCursor)
 */
val Column<*, *>.qualifiedName: String
    get() = "${table.name}.$name"