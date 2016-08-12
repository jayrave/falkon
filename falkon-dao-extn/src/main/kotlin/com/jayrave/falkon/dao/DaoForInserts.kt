package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.insert.AdderOrEnder
import com.jayrave.falkon.dao.insert.InsertBuilder
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.mapper.Column

fun <T: Any, ID : Any> Dao<T, ID>.insert(t: T) {
    insert(listOf(t))
}


fun <T: Any, ID : Any> Dao<T, ID>.insert(vararg ts: T) {
    insert(ts.asList())
}


fun <T: Any, ID : Any> Dao<T, ID>.insert(ts: Iterable<T>) {
    val orderedColumns = OrderedColumns.forAllColumnsOf(table)
    throwIfOrderedColumnsIsEmpty(orderedColumns)

    table.configuration.engine.executeInTransaction {
        var compiledStatementForInsert: CompiledStatement<Int>? = null
        try {
            for (item in ts) {
                compiledStatementForInsert = when (compiledStatementForInsert) {

                    // First item. Build CompiledStatement for insert
                    null -> buildCompiledStatementForInsert(item, orderedColumns, insertBuilder())

                    // Not the first item. Clear bindings & rebind all columns
                    else -> {
                        compiledStatementForInsert.clearBindings()
                        bindAllColumns(item, orderedColumns, compiledStatementForInsert)
                        compiledStatementForInsert
                    }
                }

                compiledStatementForInsert.execute()
            }
        } finally {
            // No matter what happens, CompiledStatement must be closed
            // to prevent resource leakage
            compiledStatementForInsert?.close()
        }
    }
}


/**
 * @param item Item to build [CompiledStatement] for
 * @param orderedColumns the list of ordered, non empty columns
 *
 * @return [CompiledStatement] corresponding to the passed in [item]
 * @throws IllegalArgumentException if the passed in [OrderedColumns] is empty
 */
private fun <T: Any> buildCompiledStatementForInsert(
        item: T, orderedColumns: OrderedColumns<T>, insertBuilder: InsertBuilder<T>):
        CompiledStatement<Int> {

    throwIfOrderedColumnsIsEmpty(orderedColumns)

    var adderOrEnder: AdderOrEnder<T>? = null
    orderedColumns.forEach {

        @Suppress("UNCHECKED_CAST")
        val column = it as Column<T, Any?>
        adderOrEnder = when (adderOrEnder) {
            null -> insertBuilder.set(column, column.extractPropertyFrom(item))
            else -> adderOrEnder!!.set(column, column.extractPropertyFrom(item))
        }
    }

    return adderOrEnder!!.compile()
}


/**
 * @param item Item to build [CompiledStatement] for
 * @param orderedColumns the list of ordered, non empty columns
 * @param compiledStatementForInsert the compiled statement to bind the columns to
 *
 * @throws IllegalArgumentException if the passed in [OrderedColumns] is empty
 */
private fun <T: Any> bindAllColumns(
        item: T, orderedColumns: OrderedColumns<T>,
        compiledStatementForInsert: CompiledStatement<Int>) {

    throwIfOrderedColumnsIsEmpty(orderedColumns)
    compiledStatementForInsert.bindOrderedColumns(orderedColumns, item)
}


private fun throwIfOrderedColumnsIsEmpty(orderedColumns: OrderedColumns<*>) {
    if (orderedColumns.isEmpty()) {
        throw IllegalArgumentException("Columns can't be empty for inserts")
    }
}