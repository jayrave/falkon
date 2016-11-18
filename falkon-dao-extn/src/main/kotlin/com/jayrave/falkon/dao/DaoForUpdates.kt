package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.update.AdderOrEnder
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.mapper.Column

/**
 * Updates a record if possible based on the primary key of [t] according to
 * the table it is connected to
 *
 * @return number of rows updated by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.update(t: T): Int {
    return update(listOf(t))
}


/**
 * Updates a record if possible based on the primary keys of each of [ts] according to
 * the table it is connected to
 *
 * @return number of rows updated by this operation
 * @see [update]
 */
fun <T : Any, ID : Any> Dao<T, ID>.update(vararg ts: T): Int {
    return update(ts.asList())
}


/**
 * Updates a record if possible based on the primary keys of each of [ts] according to
 * the table it is connected to
 *
 * @return number of rows updated by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.update(ts: Iterable<T>): Int {
    var numberOfRowsUpdated = 0
    val idColumns = table.idColumns
    val nonIdColumns = table.nonIdColumns

    throwIfColumnCollectionIsEmpty(idColumns, "id")
    if (nonIdColumns.isNotEmpty()) {
        table.configuration.engine.executeInTransaction {
            var compiledStatementForUpdate: CompiledStatement<Int>? = null
            try {
                for (item in ts) {
                    compiledStatementForUpdate = when (compiledStatementForUpdate) {

                        // First item. Build CompiledStatement for update
                        null -> buildCompiledStatementForUpdate(item, idColumns, nonIdColumns)

                        // Not the first item. Clear bindings, rebind required columns & set id
                        else -> {
                            compiledStatementForUpdate.clearBindings()
                            compiledStatementForUpdate.bindColumns(nonIdColumns, item)
                            compiledStatementForUpdate.bindColumns(
                                    idColumns, item, nonIdColumns.size + 1
                            )

                            compiledStatementForUpdate
                        }
                    }

                    numberOfRowsUpdated += compiledStatementForUpdate.execute()
                }
            } finally {
                // No matter what happens, CompiledStatement must be closed
                // to prevent resource leakage
                compiledStatementForUpdate?.close()
            }
        }
    }

    return numberOfRowsUpdated
}


/**
 * @param item Item to build [CompiledStatement] for
 * @param idColumns list of id, non empty columns with deterministic iteration order
 * @param nonIdColumns list of non-id, non empty columns with deterministic iteration order
 *
 * @return [CompiledStatement] corresponding to the passed in [item]
 * @throws IllegalArgumentException if the passed in [nonIdColumns] is empty
 */
private fun <T : Any> Dao<T, *>.buildCompiledStatementForUpdate(
        item: T, idColumns: Collection<Column<T, *>>, nonIdColumns: Collection<Column<T, *>>):
        CompiledStatement<Int> {

    throwIfColumnCollectionIsEmpty(idColumns, "id")
    throwIfColumnCollectionIsEmpty(nonIdColumns, "non-id")

    val updateBuilder = updateBuilder()

    // Set all non-id columns
    var adderOrEnder: AdderOrEnder<T>? = null
    nonIdColumns.forEach {

        @Suppress("UNCHECKED_CAST")
        val column = it as Column<T, Any?>
        adderOrEnder = when (adderOrEnder) {
            null -> updateBuilder.set(column, column.extractPropertyFrom(item))
            else -> adderOrEnder!!.set(column, column.extractPropertyFrom(item))
        }
    }

    // Add predicates for id columns
    adderOrEnder!!.where().and {
        idColumns.forEach { idColumn ->

            @Suppress("UNCHECKED_CAST")
            eq(idColumn as Column<T, Any?>, idColumn.extractPropertyFrom(item))
        }
    }

    return adderOrEnder!!.compile()
}


private fun throwIfColumnCollectionIsEmpty(columns: Collection<*>, kind: String) {
    if (columns.isEmpty()) {
        throw IllegalArgumentException("$kind columns can't be empty for updates")
    }
}