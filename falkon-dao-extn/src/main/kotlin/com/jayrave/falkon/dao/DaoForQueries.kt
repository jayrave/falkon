package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.lib.extractAllModelsAndClose
import com.jayrave.falkon.dao.lib.extractFirstModelAndClose
import com.jayrave.falkon.dao.lib.uniqueNameInDb

/**
 * @return [T] that has the passed in [ID] as its primary key
 */
fun <T: Any, ID : Any> Dao<T, ID>.findById(id: ID): T? {
    return queryBuilder()
            .where()
            .eq(table.idColumn, id)
            .limit(1) // to be defensive
            .compile()
            .extractFirstModelAndClose(table) { it.uniqueNameInDb }
}


/**
 * @return all records of this table converted into [T]s
 */
fun <T: Any, ID : Any> Dao<T, ID>.findAll(): List<T> {
    return queryBuilder()
            .compile()
            .extractAllModelsAndClose(table) { it.uniqueNameInDb }
}