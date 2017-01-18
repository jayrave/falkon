package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.mapper.ReadOnlyColumnOfTable
import java.sql.SQLException

/**
 * A convenience function to select multiple columns in one go
 *
 * @param [columns] to be selected
 * @param [aliaser] used to compute the alias to be used for the selected columns
 *
 * @throws [SQLException] if [columns] is empty
 */
fun <Z : AdderOrEnder<Z>> AdderOrEnder<Z>.select(
        columns: Iterable<ReadOnlyColumnOfTable<*, *>>,
        aliaser: ((ReadOnlyColumnOfTable<*, *>) -> String)? = null): Z {

    var result: Z? = null
    columns.forEach { column ->
        result = select(column, aliaser?.invoke(column))
    }

    if (result == null) {
        throw SQLException("Columns can't be empty")
    }

    return result!!
}