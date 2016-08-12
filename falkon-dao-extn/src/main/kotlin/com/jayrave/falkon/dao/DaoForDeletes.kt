package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.bind

/**
 * @return number of rows deleted by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.delete(t: T): Int {
    return delete(listOf(t))
}


/**
 * @return number of rows deleted by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.delete(vararg ts: T): Int {
    return delete(ts.asList())
}


/**
 * @return number of rows deleted by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.delete(ts: Iterable<T>): Int {
    return deleteByIdImpl(ts) { table.extractIdFrom(it) }
}


/**
 * @return number of rows deleted by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.deleteById(id: ID): Int {
    return deleteById(listOf(id))
}


/**
 * @return number of rows deleted by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.deleteById(vararg ids: ID): Int {
    return deleteById(ids.asList())
}


/**
 * @return number of rows deleted by this operation
 */
fun <T: Any, ID : Any> Dao<T, ID>.deleteById(ids: Iterable<ID>): Int {
    return deleteByIdImpl(ids) { it }
}


/**
 * @return number of rows deleted by this operation
 */
private fun <T: Any, ID : Any, ITEM> Dao<T, ID>.deleteByIdImpl(
        items: Iterable<ITEM>, idExtractor: (ITEM) -> ID): Int {
    
    var numberOfRowsDeleted = 0
    var compiledStatementForDelete: CompiledStatement<Int>? = null

    table.configuration.engine.executeInTransaction {
        try {
            items.forEach {
                compiledStatementForDelete = when (compiledStatementForDelete) {

                    // First item. Build CompiledStatement for delete
                    null -> deleteBuilder()
                            .where()
                            .eq(table.idColumn, idExtractor.invoke(it))
                            .compile()

                    // Not the first item. Just clear bindings for CompiledStatement & rebind id
                    else -> {
                        compiledStatementForDelete!!.clearBindings()
                        compiledStatementForDelete!!.bind(1, idExtractor.invoke(it))
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