package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.mapper.Column

/**
 * Gives the qualified name of a column. It is of the format "table_name.column_name".
 * This form is NOT safe to use as the column's alias in queries as some db engine
 * don't like this (eg., Android's built-in SQLite via SQLiteCursor)
 *
 * @see [uniqueNameInDb]
 */
val Column<*, *>.qualifiedName: String
    get() = "${table.name}.$name"


/**
 * Gives the unique name for this column in the database it belongs to. It is of
 * the format "table_name_column_name". This form is safe to use as the column's
 * alias in queries
 *
 * @see [qualifiedName]
 */
val Column<*, *>.uniqueNameInDb: String
    get() = "${table.name}_$name"