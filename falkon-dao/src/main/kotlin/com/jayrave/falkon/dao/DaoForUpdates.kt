package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.dao.update.AdderOrEnder
import com.jayrave.falkon.dao.update.UpdateBuilder
import com.jayrave.falkon.engine.CompiledUpdate

/**
 * @return number of rows updated by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.update(t: T): Int {
    return update(listOf(t))
}


/**
 * @return number of rows updated by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.update(vararg ts: T): Int {
    return update(ts.asList())
}


/**
 * @return number of rows updated by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.update(ts: Iterable<T>): Int {
    var numberOfRowsAffected = 0
    val engine = table.configuration.engine
    val orderedNonIdColumns = OrderedColumns.forNonIdColumnsOf(table)

    if (orderedNonIdColumns.isNotEmpty()) {
        engine.executeInTransaction {
            var compiledUpdate: CompiledUpdate? = null
            try {
                for (item in ts) {
                    compiledUpdate = when (compiledUpdate) {

                        // First item. Build CompiledUpdate
                        null -> buildCompiledUpdate(
                                item, table.idColumn, orderedNonIdColumns, updateBuilder()
                        )

                        // Not the first item. Clear bindings & rebind required columns
                        else -> {
                            compiledUpdate.clearBindings() // Not required, but being defensive
                            bindAllNonIdColumns(item, orderedNonIdColumns, compiledUpdate)
                            compiledUpdate
                        }
                    }

                    numberOfRowsAffected += compiledUpdate.execute()
                }
            } finally {
                // No matter what happens, CompiledUpdate must be closed
                // to prevent resource leakage
                compiledUpdate?.close()
            }
        }
    }

    return numberOfRowsAffected
}


/**
 * @param item Item to build [CompiledUpdate] for
 * @param orderedNonIdColumns the list of ordered, non-id, non empty columns
 *
 * @return [CompiledUpdate] if there is at least one column other than the ID column else null
 * @throws IllegalArgumentException if the passed in [OrderedColumns] is empty
 */
private fun <T: Any, ID: Any> buildCompiledUpdate(
        item: T, idColumn: Column<T, ID>, orderedNonIdColumns: OrderedColumns<T>,
        updateBuilder: UpdateBuilder<T>): CompiledUpdate {

    throwIfOrderedNonIdColumnsIsEmpty(orderedNonIdColumns)

    var adderOrEnder: AdderOrEnder<T>? = null
    orderedNonIdColumns.forEach {

        @Suppress("UNCHECKED_CAST")
        val column = it as Column<T, Any?>
        adderOrEnder = when (adderOrEnder) {
            null -> updateBuilder.set(column, column.extractPropertyFrom(item))
            else -> adderOrEnder!!.set(column, column.extractPropertyFrom(item))
        }
    }

    return adderOrEnder!!
            .where()
            .eq(idColumn, idColumn.extractPropertyFrom(item))
            .build()
}


/**
 * @param item Item to build [CompiledUpdate] for
 * @param orderedNonIdColumns the list of ordered, non-id, non empty columns
 * @param compiledUpdate the compiled statement to bind the columns to
 *
 * @throws IllegalArgumentException if the passed in [OrderedColumns] is empty
 */
private fun <T: Any> bindAllNonIdColumns(
        item: T, orderedNonIdColumns: OrderedColumns<T>, compiledUpdate: CompiledUpdate) {

    throwIfOrderedNonIdColumnsIsEmpty(orderedNonIdColumns)
    orderedNonIdColumns.forEachIndexed { index, column ->
        // index + 1 since compiled statement index is 1-based
        compiledUpdate.bindColumn(index + 1, column, item)
    }
}


private fun throwIfOrderedNonIdColumnsIsEmpty(orderedNonIdColumns: OrderedColumns<*>) {
    if (orderedNonIdColumns.isEmpty()) {
        throw IllegalArgumentException("Non ID columns can't be empty for updates")
    }
}