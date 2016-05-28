package com.jayrave.falkon.dao.update

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.engine.Sink

interface UpdateBuilder<T : Any, S : Sink> {

    val table: Table<T, *, *, S>

    /**
     * Calling this method again for a column that has already been set should overwrite the existing value
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>
}


interface AdderOrEnder<T : Any> {

    /**
     * @see [UpdateBuilder.set]
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>

    /**
     * Use to build the WHERE clause of UPDATE SQL statement. Each call would erase the previously configured
     * WHERE clause and start creating a new one
     */
    fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>>

    /**
     * @return - Number of rows affected by this update operation
     */
    fun update(): Int
}


/**
 * To access some [AdderOrEnder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface PredicateAdderOrEnder<T : Any> : com.jayrave.falkon.dao.where.AdderOrEnder<T, PredicateAdderOrEnder<T>> {
    fun update(): Int
}