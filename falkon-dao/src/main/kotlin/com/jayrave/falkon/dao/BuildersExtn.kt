package com.jayrave.falkon.dao

import com.jayrave.falkon.mapper.Column
import java.sql.SQLException

// ------------------------------------------- Query -----------------------------------------------

// A #query convenience function is not included here as it doesn't make sense to.
// Source that is returned from a CompiledStatement<Source> could end up not working
// if the CompiledStatement<Source> itself is closed

/**
 * A convenience function to select a list of columns
 * *Beware:* Every time this method is called an array as long as the passed in list is created
 */
fun <T : Any, Z : com.jayrave.falkon.dao.query.AdderOrEnder<T, Z>>
        com.jayrave.falkon.dao.query.AdderOrEnder<T, Z>.select(
        columns: Collection<Column<T, *>>): Z {

    return when (columns.isEmpty()) {
        true -> throw SQLException("Columns can't be empty")
        else -> {
            val columnsCount = columns.size
            when (columnsCount == 1) {
                true -> this.select(columns.first())
                else -> {
                    val iterator = columns.iterator()
                    val firstColumn = iterator.next()
                    val remainingColumns = Array(columnsCount - 1) { iterator.next() }
                    this.select(firstColumn, *remainingColumns)
                }
            }
        }
    }
}


/**
 * A convenience function to select a list of columns
 * *Beware:* Every time this method is called an array as long as the passed in list is created
 */
fun <Z : com.jayrave.falkon.dao.query.lenient.AdderOrEnder<Z>>
        com.jayrave.falkon.dao.query.lenient.AdderOrEnder<Z>.select(
        columns: Collection<Column<*, *>>): Z {

    return when (columns.isEmpty()) {
        true -> throw SQLException("Columns can't be empty")
        else -> {
            val columnsCount = columns.size
            when (columnsCount == 1) {
                true -> this.select(columns.first())
                else -> {
                    val iterator = columns.iterator()
                    val firstColumn = iterator.next()
                    val remainingColumns = Array(columnsCount - 1) { iterator.next() }
                    this.select(firstColumn, *remainingColumns)
                }
            }
        }
    }
}

// ------------------------------------------- Query -----------------------------------------------