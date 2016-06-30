package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.engine.CompiledDelete

interface DeleteBuilder<T : Any> {

    val table: Table<T, *, *>

    /**
     * Use to build the WHERE clause of DELETE SQL statement. Each call would erase the
     * previously configured WHERE clause and start creating a new one
     */
    fun where(): WhereBuilder<T, AdderOrEnder<T>>

    /**
     * @return [CompiledDelete] for this [DeleteBuilder]
     */
    fun compile(): CompiledDelete
}


/**
 * To access some [DeleteBuilder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface AdderOrEnder<T : Any> : com.jayrave.falkon.dao.where.AdderOrEnder<T, AdderOrEnder<T>> {
    fun compile(): CompiledDelete
}