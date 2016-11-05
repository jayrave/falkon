package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.update.AdderOrEnder
import com.jayrave.falkon.dao.update.UpdateBuilder
import com.jayrave.falkon.engine.CompiledStatement
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
    val idColumn = table.idColumn
    val nonIdColumns = table.allColumns.filter { it != idColumn }

    if (nonIdColumns.isNotEmpty()) {
        table.configuration.engine.executeInTransaction {
            var compiledStatementForUpdate: CompiledStatement<Int>? = null
            try {
                for (item in ts) {
                    compiledStatementForUpdate = when (compiledStatementForUpdate) {

                        // First item. Build CompiledStatement for update
                        null -> buildCompiledStatementForUpdate(
                                item, idColumn, nonIdColumns, updateBuilder()
                        )

                        // Not the first item. Clear bindings, rebind required columns & set id
                        else -> {
                            compiledStatementForUpdate.clearBindings()
                            compiledStatementForUpdate.bindColumns(nonIdColumns, item)
                            compiledStatementForUpdate.bind(
                                    nonIdColumns.size + 1, table.extractIdFrom(item)
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
 * @param nonIdColumns list of non-id, non empty columns with deterministic iteration order
 *
 * @return [CompiledStatement] corresponding to the passed in [item]
 * @throws IllegalArgumentException if the passed in [nonIdColumns] is empty
 */
private fun <T: Any, ID: Any> buildCompiledStatementForUpdate(
        item: T, idColumn: Column<T, ID>, nonIdColumns: Collection<Column<T, *>>,
        updateBuilder: UpdateBuilder<T>): CompiledStatement<Int> {

    if (nonIdColumns.isEmpty()) {
        throw IllegalArgumentException("Non ID columns can't be empty for updates")
    }

    var adderOrEnder: AdderOrEnder<T>? = null
    nonIdColumns.forEach {

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