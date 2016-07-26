package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.engine.CompiledQuery
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table

/**
 * All column names will be qualified in the built SELECT SQL statement
 */
interface QueryBuilder<T : Any> : AdderOrEnder<T, QueryBuilder<T>> {

    val table: Table<T, *>

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
     * Add columns to be included in the result set. This method can be called multiple times
     * to add more columns to the result set. Behaviour on calling this method again for a column
     * that has already been included is implementation dependent
     *
     * *NOTE:* If this isn't called, by default all columns will be included in the result set
     */
    fun select(column: Column<T, *>, vararg others: Column<T, *>): Z

    /**
     * Adds JOIN clause. Can be called multiple times to add more tables to the JOIN clause.
     * Only simple JOINS are possible using this builder. If you need to specify WHERE clauses
     * on the non-primary tables involved in the JOIN, consider using
     * [com.jayrave.falkon.dao.query.lenient.QueryBuilder]
     */
    fun join(column: Column<T, *>, onColumn: Column<*, *>): Z

    /**
     * Adds the given columns to GROUP BY clause. This method can be called multiple times
     * to add more columns to the GROUP BY clause. Columns are added to the GROUP BY clause
     * in a "first come, first serve" order. Behaviour on calling this method again for a
     * column that has already been included is implementation dependent
     */
    fun groupBy(column: Column<T, *>, vararg others: Column<T, *>): Z

    /**
     * Adds ORDER BY clause for the passed in column. This method can be called multiple times
     * to add more columns to the ORDER BY clause. Columns are added to the ORDER BY clause in
     * a "first come, first serve" order. Behaviour on calling this method again for a
     * column that has already been included is implementation dependent
     */
    fun orderBy(column: Column<T, *>, ascending: Boolean): Z

    /**
     * Adds LIMIT clause which dictates the maximum number of rows the query can return.
     * Calling this method again would overwrite the previously configured limit
     */
    fun limit(count: Long): Z

    /**
     * Adds OFFSET clause which dictates how many rows should be skipped in the result set.
     * Calling this method again would overwrite the previously configured offset
     */
    fun offset(count: Long): Z

    /**
     * @return [Query] for this [QueryBuilder]
     */
    fun build(): Query

    /**
     * @return [CompiledQuery] for this [QueryBuilder]
     */
    fun compile(): CompiledQuery
}


interface AdderOrEnderAfterWhere<T : Any> : AdderOrEnder<T, AdderOrEnderAfterWhere<T>>


/**
 * To access some [QueryBuilder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface PredicateAdderOrEnder<T : Any> :
        com.jayrave.falkon.dao.where.AdderOrEnder<T, PredicateAdderOrEnder<T>>,
        AdderOrEnderAfterWhere<T>