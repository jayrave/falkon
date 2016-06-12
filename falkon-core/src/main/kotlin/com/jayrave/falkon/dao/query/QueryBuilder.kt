package com.jayrave.falkon.dao.query

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.engine.CompiledQuery

interface QueryBuilder<T : Any> : AdderOrEnder<T, QueryBuilder<T>> {

    val table: Table<T, *, *>

    /**
     * Use to build the WHERE clause of SELECT SQL statement. Each call would erase the
     * previously configured WHERE clause and start creating a new one
     */
    fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>>
}


interface AdderOrEnder<T : Any, Z : AdderOrEnder<T, Z>> {

    /**
     * Adds DISTINCT clause to the SQL query statement. This call is Idempotent
     */
    fun distinct(): Z

    /**
     * Add columns to be included in the result set. Columns which are already added are skipped
     * NOTE: If this isn't called, by default all columns will be included in the result set
     */
    fun select(column: Column<T, *>, vararg others: Column<T, *>): Z

    /**
     * Adds the given columns to GROUP BY clause. Columns which are already in the GROUP BY clause
     * are skipped. Columns are added to the GROUP BY clause in a "first come, first serve" order
     */
    fun groupBy(column: Column<T, *>, vararg others: Column<T, *>): Z

    /**
     * Adds ORDER BY clause for the passed in column. If called for a column that has already been
     * included in the ORDER BY clause, this is a no-op. Multiple columns can be added by calling
     * this multiple times with different [Column]s. Columns are added to the ORDER BY clause in
     * a "first come, first serve" order
     */
    fun orderBy(column: Column<T, *>, ascending: Boolean): Z

    /**
     * Adds LIMIT clause which dictates the maximum number of rows the query can return
     */
    fun limit(count: Long): Z

    /**
     * Adds OFFSET clause which dictates how many rows should be skipped in the result set
     */
    fun offset(count: Long): Z

    /**
     * @return [CompiledQuery] for this [QueryBuilder]
     */
    fun build(): CompiledQuery
}


interface AdderOrEnderAfterWhere<T : Any> : AdderOrEnder<T, AdderOrEnderAfterWhere<T>>


/**
 * To access some [QueryBuilder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface PredicateAdderOrEnder<T : Any> :
        com.jayrave.falkon.dao.where.AdderOrEnder<T, PredicateAdderOrEnder<T>>,
        AdderOrEnderAfterWhere<T>