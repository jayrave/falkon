package com.jayrave.falkon.dao.where.lenient

import com.jayrave.falkon.dao.query.Query
import com.jayrave.falkon.mapper.Column

/**
 * Interface which can be used to build the WHERE clause of a SQL statement
 */
interface WhereBuilder<Z : AdderOrEnder<Z>> :
        PredicateAdder<Z>,
        CompoundConnectorAdder<Z>


/**
 * Interface that exposes methods which will add a predicate to the WHERE clause of
 * a SQL statement
 */
interface PredicateAdder<Z : AdderOrEnder<Z>> {
    fun <T : Any, C> eq(column: Column<T, C>, value: C): Z
    fun <T : Any, C> notEq(column: Column<T, C>, value: C): Z
    fun <T : Any, C> gt(column: Column<T, C>, value: C): Z
    fun <T : Any, C> ge(column: Column<T, C>, value: C): Z
    fun <T : Any, C> lt(column: Column<T, C>, value: C): Z
    fun <T : Any, C> le(column: Column<T, C>, value: C): Z
    fun <T : Any, C> between(column: Column<T, C>, low: C, high: C): Z
    fun <T : Any, C> like(column: Column<T, C>, pattern: String): Z

    /**
     * [subQuery] should only return a single column of result
     */
    fun <T : Any, C> isIn(column: Column<T, C>, subQuery: Query): Z
    fun <T : Any, C> isIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z

    /**
     * [subQuery] should only return a single column of result
     */
    fun <T : Any, C> isNotIn(column: Column<T, C>, subQuery: Query): Z
    fun <T : Any, C> isNotIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z
    fun <T : Any, C> isNull(column: Column<T, C>): Z
    fun <T : Any, C> isNotNull(column: Column<T, C>): Z
}


/**
 * Interface that exposes methods to either add AND or OR between predicates
 */
interface SimpleConnectorAdder<Z : AdderOrEnder<Z>> {
    fun and(): AfterSimpleConnectorAdder<Z>
    fun or(): AfterSimpleConnectorAdder<Z>
}


/**
 * Interface that exposes methods to group predicates and combine them
 */
interface CompoundConnectorAdder<Z : AdderOrEnder<Z>> {
    fun and(predicate: InnerAdder.() -> Any?): Z
    fun or(predicate: InnerAdder.() -> Any?): Z
}


/**
 * Just a semantic extension of [SimpleConnectorAdder] that doesn't add any other method.
 * This most probably will be extended where [WhereBuilder] needs to be extended
 */
interface AdderOrEnder<Z : AdderOrEnder<Z>> : SimpleConnectorAdder<Z>


/**
 * Just a combination of [PredicateAdder] & [CompoundConnectorAdder] which gets returned
 * by methods of [SimpleConnectorAdder] to allow adding more predicates
 */
interface AfterSimpleConnectorAdder<Z : AdderOrEnder<Z>> :
        PredicateAdder<Z>,
        CompoundConnectorAdder<Z>


/**
 * The interface that will be exposed inside function objects passed to methods in
 * [CompoundConnectorAdder] to add more predicates to the WHERE clause
 */
interface InnerAdder {
    fun <T : Any, C> eq(column: Column<T, C>, value: C)
    fun <T : Any, C> notEq(column: Column<T, C>, value: C)
    fun <T : Any, C> gt(column: Column<T, C>, value: C)
    fun <T : Any, C> ge(column: Column<T, C>, value: C)
    fun <T : Any, C> lt(column: Column<T, C>, value: C)
    fun <T : Any, C> le(column: Column<T, C>, value: C)
    fun <T : Any, C> between(column: Column<T, C>, low: C, high: C)
    fun <T : Any, C> like(column: Column<T, C>, pattern: String)

    /**
     * [subQuery] should only return a single column of result
     */
    fun <T : Any, C> isIn(column: Column<T, C>, subQuery: Query)
    fun <T : Any, C> isIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C)

    /**
     * [subQuery] should only return a single column of result
     */
    fun <T : Any, C> isNotIn(column: Column<T, C>, subQuery: Query)
    fun <T : Any, C> isNotIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C)
    fun <T : Any, C> isNull(column: Column<T, C>)
    fun <T : Any, C> isNotNull(column: Column<T, C>)

    fun and(predicate: InnerAdder.() -> Any?)
    fun or(predicate: InnerAdder.() -> Any?)
}