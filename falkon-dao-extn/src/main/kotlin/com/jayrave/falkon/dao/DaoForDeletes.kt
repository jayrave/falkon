package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.mapper.Column

/**
 * Deletes record with the same primary key as [t]
 *
 * @return number of rows deleted by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.delete(t: T): Int {
    return delete(listOf(t))
}


/**
 * Deletes records with the same primary keys as [ts]
 *
 * @return number of rows deleted by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.delete(vararg ts: T): Int {
    return delete(ts.asList())
}


/**
 * Deletes records with the same primary keys as [ts]
 *
 * @return number of rows deleted by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.delete(ts: Iterable<T>): Int {
    return deleteByIdImpl(ts) { t, column ->
        column.extractPropertyFrom(t)
    }
}


/**
 * Deletes records with the primary key [id]
 *
 * @return number of rows deleted by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.deleteById(id: ID): Int {
    return deleteById(listOf(id))
}


/**
 * Deletes records with the primary keys [ids]
 *
 * @return number of rows deleted by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.deleteById(vararg ids: ID): Int {
    return deleteById(ids.asList())
}


/**
 * Deletes records with the primary keys [ids]
 *
 * @return number of rows deleted by this operation
 */
fun <T : Any, ID : Any> Dao<T, ID>.deleteById(ids: Iterable<ID>): Int {
    return deleteByIdImpl(ids) { id, column ->
        table.extractFrom(id, column)
    }
}


/**
 * @return number of rows deleted by this operation
 */
private fun <T : Any, ITEM : Any> Dao<T, *>.deleteByIdImpl(
        items: Iterable<ITEM>, idExtractor: (ITEM, Column<T, *>) -> Any?): Int {

    val idColumns = table.idColumns
    throwIfIdColumnCollectionIsEmpty(idColumns)

    var numberOfRowsDeleted = 0

    table.configuration.engine.executeInTransaction {
        var compiledStatementForDelete: CompiledStatement<Int>? = null
        try {
            items.forEach { item ->
                compiledStatementForDelete = when (compiledStatementForDelete) {

                    // First item. Build CompiledStatement for delete
                    null -> buildCompiledStatementForDelete(idColumns, item, idExtractor)

                    // Not the first item. Just clear bindings for CompiledStatement & rebind id
                    else -> {
                        compiledStatementForDelete!!.clearBindings()
                        compiledStatementForDelete!!.bindColumns(
                                idColumns, item, 1, idExtractor
                        )
                    }
                }

                numberOfRowsDeleted += compiledStatementForDelete!!.execute()
            }

        } finally {
            // No matter what happens, CompiledStatement must be closed
            // to prevent resource leakage
            compiledStatementForDelete?.close()
        }
    }

    return numberOfRowsDeleted
}


/**
 * @param item Item to build [CompiledStatement] for
 * @param columns list of non empty columns with deterministic iteration order
 *
 * @return [CompiledStatement] corresponding to the passed in [item]
 * @throws IllegalArgumentException if the passed in [columns] is empty
 */
private fun <T : Any, ITEM : Any> Dao<T, *>.buildCompiledStatementForDelete(
        columns: Collection<Column<T, *>>, item: ITEM,
        valueExtractor: (ITEM, Column<T, *>) -> Any?):
        CompiledStatement<Int> {

    throwIfIdColumnCollectionIsEmpty(columns)

    val deleteBuilder = deleteBuilder()
    deleteBuilder.where().and {
        columns.forEach { column ->

            @Suppress("UNCHECKED_CAST")
            eq(column as Column<T, Any?>, valueExtractor.invoke(item, column))
        }
    }

    return deleteBuilder.compile()
}


private fun throwIfIdColumnCollectionIsEmpty(columns: Collection<*>) {
    if (columns.isEmpty()) {
        throw IllegalArgumentException("Id columns can't be empty for deletes")
    }
}