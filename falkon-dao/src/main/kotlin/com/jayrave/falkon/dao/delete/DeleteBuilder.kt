package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.mapper.Table

interface DeleteBuilder<T : Any> {

    val table: Table<T, *>

    /**
     * Use to build the WHERE clause of DELETE SQL statement. Each call would erase the
     * previously configured WHERE clause and start creating a new one
     */
    fun where(): WhereBuilder<T, AdderOrEnder<T>>

    /**
     * @return [Delete] for this [DeleteBuilder]
     */
    fun build(): Delete

    /**
     * @return [CompiledStatement] for this [DeleteBuilder]
     */
    fun compile(): CompiledStatement<Int>

    /**
     * @return number of rows affected by this delete operation
     */
    fun delete(): Int
}


/**
 * To access some [DeleteBuilder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface AdderOrEnder<T : Any> : com.jayrave.falkon.dao.where.AdderOrEnder<T, AdderOrEnder<T>> {
    fun build(): Delete
    fun compile(): CompiledStatement<Int>
    fun delete(): Int
}