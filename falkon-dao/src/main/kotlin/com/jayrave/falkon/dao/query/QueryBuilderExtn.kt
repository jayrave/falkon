package com.jayrave.falkon.dao.query

import com.jayrave.falkon.mapper.Column
import java.sql.SQLException

/**
 * A convenience function to select columns
 */
fun <T : Any, Z : AdderOrEnder<T, Z>> AdderOrEnder<T, Z>.select(
        columns: Iterable<Column<T, *>>): Z {

    var result: Z? = null
    columns.forEach {
        result = select(it)
    }

    if (result == null) {
        throw SQLException("Columns can't be empty")
    }

    return result!!
}