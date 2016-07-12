package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.update.AdderOrEnder
import com.jayrave.falkon.dao.update.UpdateBuilder
import com.jayrave.falkon.engine.CompiledUpdate
import com.jayrave.falkon.engine.bind
import com.jayrave.falkon.mapper.Column

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
    var numberOfRowsUpdated = 0
    val orderedNonIdColumns = OrderedColumns.forNonIdColumnsOf(table)

    if (orderedNonIdColumns.isNotEmpty()) {
        table.configuration.engine.executeInTransaction {
            var compiledUpdate: CompiledUpdate? = null
            try {
                for (item in ts) {
                    compiledUpdate = when (compiledUpdate) {

                        // First item. Build CompiledUpdate
                        null -> buildCompiledUpdate(
                                item, table.idColumn, orderedNonIdColumns, updateBuilder()
                        )

                        // Not the first item. Clear bindings, rebind required columns & set id
                        else -> {
                            compiledUpdate.clearBindings()
                            bindAllNonIdColumns(item, orderedNonIdColumns, compiledUpdate)
                            compiledUpdate.bind(
                                    orderedNonIdColumns.size + 1, table.extractIdFrom(item)
                            )

                            compiledUpdate
                        }
                    }

                    numberOfRowsUpdated += compiledUpdate.execute()
                }
            } finally {
                // No matter what happens, CompiledUpdate must be closed
                // to prevent resource leakage
                compiledUpdate?.close()
            }
        }
    }

    return numberOfRowsUpdated
}


/**
 * @param item Item to build [CompiledUpdate] for
 * @param orderedNonIdColumns the list of ordered, non-id, non empty columns
 *
 * @return [CompiledUpdate] corresponding to the passed in [item]
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
            .compile()
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
    compiledUpdate.bindOrderedColumns(orderedNonIdColumns, item)
}


private fun throwIfOrderedNonIdColumnsIsEmpty(orderedNonIdColumns: OrderedColumns<*>) {
    if (orderedNonIdColumns.isEmpty()) {
        throw IllegalArgumentException("Non ID columns can't be empty for updates")
    }
}