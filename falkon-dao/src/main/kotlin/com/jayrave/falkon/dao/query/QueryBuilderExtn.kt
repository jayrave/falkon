package com.jayrave.falkon.dao.query

import com.jayrave.falkon.mapper.Column
import java.sql.SQLException

/**
 * A convenience function to select multiple columns in one go
 *
 * @param [columns] to be selected
 * @param [aliaser] used to compute the alias to be used for the selected columns
 *
 * @throws [SQLException] if [columns] is empty
 */
fun <T : Any, Z : AdderOrEnder<T, Z>> AdderOrEnder<T, Z>.select(
        columns: Iterable<Column<T, *>>, aliaser: ((Column<T, *>) -> String)? = null): Z {

    var result: Z? = null
    columns.forEach { column ->
        result = select(column, aliaser?.invoke(column))
    }

    if (result == null) {
        throw SQLException("Columns can't be empty")
    }

    return result!!
}