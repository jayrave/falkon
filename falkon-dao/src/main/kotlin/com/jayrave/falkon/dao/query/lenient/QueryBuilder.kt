package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.query.JoinType
import com.jayrave.falkon.dao.query.Query
import com.jayrave.falkon.dao.where.lenient.WhereBuilder
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.mapper.ReadOnlyColumnOfTable
import com.jayrave.falkon.mapper.Table

interface QueryBuilder {

    /**
     * The name of the table this builder deals with. If this is going to end as a query
     * with JOIN clauses, this table would serve as the left table of the first join
     */
    fun fromTable(table: Table<*, *>): AdderOrEnderBeforeWhere
}


interface AdderOrEnder<Z : AdderOrEnder<Z>> {

    /**
     * Adds DISTINCT clause to the SQL query statement. This call is Idempotent
     */
    fun distinct(): Z

    /**
     * Add column to be included in the result set. This method can be called multiple times
     * to add more columns to the result set. Behaviour on calling this method again for a column
     * that has already been included is implementation dependent
     *
     * *NOTE:* By default all columns (including columns of tables in the JOIN clause if any)
     * will be included in the result set. This works similar to `*` projection
     *
     * @param [column] to be included in the result set
     * @param [alias] the name by which [column] will be addressable in the result set
     */
    fun select(column: String, alias: String? = null): Z
    fun select(column: ReadOnlyColumnOfTable<*, *>, alias: String? = null): Z

    /**
     * Adds JOIN clause. Can be called multiple times to add more tables to the JOIN clause
     *
     * *NOTE:* Default join type is [JoinType.INNER_JOIN]
     * *NOTE:* When using joins it is good practice to [select] the required columns and use
     * appropriate aliases for those columns to prevent name collisions in the result set
     */
    fun join(
            column: ReadOnlyColumnOfTable<*, *>, onColumn: ReadOnlyColumnOfTable<*, *>,
            joinType: JoinType = JoinType.INNER_JOIN
    ): Z

    /**
     * Adds the given columns to GROUP BY clause. This method can be called multiple times
     * to add more columns to the GROUP BY clause. Columns are added to the GROUP BY clause
     * in a "first come, first serve" order. Behaviour on calling this method again for a
     * column that has already been included is implementation dependent
     */
    fun groupBy(column: ReadOnlyColumnOfTable<*, *>, vararg others: ReadOnlyColumnOfTable<*, *>): Z

    /**
     * Adds ORDER BY clause for the passed in column. This method can be called multiple times
     * to add more columns to the ORDER BY clause. Columns are added to the ORDER BY clause in
     * a "first come, first serve" order. Behaviour on calling this method again for a
     * column that has already been included is implementation dependent
     */
    fun orderBy(column: ReadOnlyColumnOfTable<*, *>, ascending: Boolean): Z

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
     * @return [CompiledStatement] for this [QueryBuilder]
     */
    fun compile(): CompiledStatement<Source>
}


interface AdderOrEnderBeforeWhere : AdderOrEnder<AdderOrEnderBeforeWhere> {

    /**
     * Use to build the WHERE clause of SELECT SQL statement. Each call would erase the
     * previously configured WHERE clause and start creating a new one
     */
    fun where(): WhereBuilder<PredicateAdderOrEnder>
}


/**
 * To make sure that [AdderOrEnderBeforeWhere.where] is seen only once if [QueryBuilder]
 * is strictly used as a fluent interface
 */
interface AdderOrEnderAfterWhere : AdderOrEnder<AdderOrEnderAfterWhere>


/**
 * To access some [QueryBuilder] methods conveniently after chaining calls on [WhereBuilder]
 */
interface PredicateAdderOrEnder :
        com.jayrave.falkon.dao.where.lenient.AdderOrEnder<PredicateAdderOrEnder>,
        AdderOrEnderAfterWhere