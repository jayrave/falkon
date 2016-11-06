package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.lib.extractAllModelsAndClose
import com.jayrave.falkon.dao.lib.extractFirstModelAndClose
import com.jayrave.falkon.dao.lib.uniqueNameInDb
import com.jayrave.falkon.mapper.Column

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
            .extractFirstModelAndClose(table) { it.uniqueNameInDb }
}


/**
 * @return all records of this table converted into [T]s
 */
fun <T : Any, ID : Any> Dao<T, ID>.findAll(): List<T> {
    return queryBuilder()
            .compile()
            .extractAllModelsAndClose(table) { it.uniqueNameInDb }
}