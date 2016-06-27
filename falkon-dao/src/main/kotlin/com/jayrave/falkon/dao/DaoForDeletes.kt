package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledDelete
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
    var compiledDelete: CompiledDelete? = null

    try {
        table.configuration.engine.executeInTransaction {
            items.forEach {
                compiledDelete = when (compiledDelete) {

                    // First item. Build CompiledDelete
                    null -> deleteBuilder()
                            .where()
                            .eq(table.idColumn, idExtractor.invoke(it))
                            .build()

                    // Not the first item. Just clear bindings for CompiledDelete & rebind id
                    else -> {
                        compiledDelete!!.clearBindings()
                        compiledDelete!!.bind(1, idExtractor.invoke(it))
                    }
                }

                numberOfRowsDeleted += compiledDelete!!.execute()
            }
        }

    }  finally {
        // No matter what happens, CompiledDelete must be closed
        // to prevent resource leakage
        compiledDelete?.close()
    }

    return numberOfRowsDeleted
}