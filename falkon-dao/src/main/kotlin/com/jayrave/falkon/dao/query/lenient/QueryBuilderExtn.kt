package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.mapper.Column
import java.sql.SQLException

/**
 * A convenience function to select a list of columns
 */
fun <Z : AdderOrEnder<Z>> AdderOrEnder<Z>.select(columns: Iterable<Column<*, *>>): Z {
    var result: Z? = null
    columns.forEach {
        result = select(it)
    }

    if (result == null) {
        throw SQLException("Columns can't be empty")
    }

    return result!!
}