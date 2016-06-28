package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.dao.insert.AdderOrEnder
import com.jayrave.falkon.dao.insert.InsertBuilder
import com.jayrave.falkon.engine.CompiledInsert

fun <T: Any, ID : Any> Dao<T, ID>.insert(t: T) {
    insert(listOf(t))
}


fun <T: Any, ID : Any> Dao<T, ID>.insert(vararg ts: T) {
    insert(ts.asList())
}


fun <T: Any, ID : Any> Dao<T, ID>.insert(ts: Iterable<T>) {
    val engine = table.configuration.engine
    val orderedColumns = OrderedColumns.forAllColumnsOf(table)

    throwIfOrderedColumnsIsEmpty(orderedColumns)
    engine.executeInTransaction {
        var compiledInsert: CompiledInsert? = null
        try {
            for (item in ts) {
                compiledInsert = when (compiledInsert) {

                    // First item. Build CompiledInsert
                    null -> buildCompiledInsert(item, orderedColumns, insertBuilder())

                    // Not the first item. Clear bindings & rebind all columns
                    else -> {
                        compiledInsert.clearBindings()
                        bindAllColumns(item, orderedColumns, compiledInsert)
                        compiledInsert
                    }
                }

                compiledInsert.execute()
            }
        } finally {
            // No matter what happens, CompiledInsert must be closed
            // to prevent resource leakage
            compiledInsert?.close()
        }
    }
}


/**
 * @param item Item to build [CompiledInsert] for
 * @param orderedColumns the list of ordered, non empty columns
 *
 * @return [CompiledInsert] corresponding to the passed in [item]
 * @throws IllegalArgumentException if the passed in [OrderedColumns] is empty
 */
private fun <T: Any> buildCompiledInsert(
        item: T, orderedColumns: OrderedColumns<T>, insertBuilder: InsertBuilder<T>):
        CompiledInsert {

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

    return adderOrEnder!!.build()
}


/**
 * @param item Item to build [CompiledInsert] for
 * @param orderedColumns the list of ordered, non empty columns
 * @param compiledInsert the compiled statement to bind the columns to
 *
 * @throws IllegalArgumentException if the passed in [OrderedColumns] is empty
 */
private fun <T: Any> bindAllColumns(
        item: T, orderedColumns: OrderedColumns<T>, compiledInsert: CompiledInsert) {

    throwIfOrderedColumnsIsEmpty(orderedColumns)
    compiledInsert.bindOrderedColumns(orderedColumns, item)
}


private fun throwIfOrderedColumnsIsEmpty(orderedColumns: OrderedColumns<*>) {
    if (orderedColumns.isEmpty()) {
        throw IllegalArgumentException("Columns can't be empty for inserts")
    }
}