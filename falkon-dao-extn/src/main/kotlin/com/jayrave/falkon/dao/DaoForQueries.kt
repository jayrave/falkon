package com.jayrave.falkon.dao

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.lib.extractAllModelsAndClose
import com.jayrave.falkon.mapper.lib.extractFirstModelAndClose

/**
 * @return [T] that has the passed in [ID] as its primary key
 */
fun <T : Any, ID : Any> Dao<T, ID>.findById(id: ID): T? {
    val idColumns = table.idColumns
    if (idColumns.isEmpty()) {
        throw IllegalArgumentException("ID columns can't be empty for #findById")
    }

    return queryBuilder()
            .where()
            .and {
                idColumns.forEach {
                    @Suppress("UNCHECKED_CAST")
                    eq(it as Column<T, Any?>, table.extractFrom(id, it))
                }
            }
            .limit(1) // to be defensive
            .compile()
            .extractFirstModelAndClose(table) { it.name }
}


/**
 * @return all records of this table converted into [T]s
 */
fun <T : Any, ID : Any> Dao<T, ID>.findAll(): List<T> {
    return queryBuilder()
            .compile()
            .extractAllModelsAndClose(table) { it.name }
}