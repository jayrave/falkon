package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.where.WhereBuilder

interface DeleteBuilder<T : Any> {

    val table: Table<T, *, *>

    /**
     * Use to build the WHERE clause of DELETE SQL statement. Each call would erase the
     * previously configured WHERE clause and start creating a new one
     */
    fun where(): WhereBuilder<T, AdderOrEnder<T>>

    /**
     * @return - Number of rows affected by this delete operation
     */
    fun delete(): Int
}


/**
 * To access some [DeleteBuilder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface AdderOrEnder<T : Any> : com.jayrave.falkon.dao.where.AdderOrEnder<T, AdderOrEnder<T>> {
    fun delete(): Int
}