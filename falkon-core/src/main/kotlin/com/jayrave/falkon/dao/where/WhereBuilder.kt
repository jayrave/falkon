package com.jayrave.falkon.dao.where

import com.jayrave.falkon.Column

/**
 * Interface which can be used to build the WHERE clause of a SQL statement
 */
interface WhereBuilder<T : Any, Z : AdderOrEnder<T, Z>> :
        PredicateAdder<T, Z>,
        CompoundConnectorAdder<T, Z>


/**
 * Interface that exposes methods which will add a predicate to the WHERE clause of
 * a SQL statement
 */
interface PredicateAdder<T : Any, Z : AdderOrEnder<T, Z>> {
    fun <C> eq(column: Column<T, C>, value: C): Z
    fun <C> notEq(column: Column<T, C>, value: C): Z
    fun <C> gt(column: Column<T, C>, value: C): Z
    fun <C> ge(column: Column<T, C>, value: C): Z
    fun <C> lt(column: Column<T, C>, value: C): Z
    fun <C> le(column: Column<T, C>, value: C): Z
    fun <C> between(column: Column<T, C>, low: C, high: C): Z
    fun <C> like(column: Column<T, C>, pattern: String): Z
    fun <C> isNull(column: Column<T, C>): Z
    fun <C> isNotNull(column: Column<T, C>): Z
}


/**
 * Interface that exposes methods to either add AND or OR between predicates
 */
interface SimpleConnectorAdder<T : Any, Z : AdderOrEnder<T, Z>> {
    fun and(): AfterSimpleConnectorAdder<T, Z>
    fun or(): AfterSimpleConnectorAdder<T, Z>
}


/**
 * Interface that exposes methods to group predicates and combine them
 */
interface CompoundConnectorAdder<T : Any, Z : AdderOrEnder<T, Z>> {
    fun and(predicate: InnerAdder<T>.() -> Any?): Z
    fun or(predicate: InnerAdder<T>.() -> Any?): Z
}


/**
 * Just a semantic extension of [SimpleConnectorAdder] that doesn't add any other method. This most
 * probably will be extended where [WhereBuilder] needs to be extended
 */
interface AdderOrEnder<T : Any, Z : AdderOrEnder<T, Z>> : SimpleConnectorAdder<T, Z>


/**
 * Just a combination of [PredicateAdder] & [CompoundConnectorAdder] which gets returned
 * by methods of [SimpleConnectorAdder] to allow adding more predicates
 */
interface AfterSimpleConnectorAdder<T : Any, Z : AdderOrEnder<T, Z>> :
        PredicateAdder<T, Z>,
        CompoundConnectorAdder<T, Z>


/**
 * The interface that will be exposed inside function objects passed to methods in [CompoundConnectorAdder]
 * to add more predicates to the WHERE clause
 */
interface InnerAdder<T : Any> {
    fun <C> eq(column: Column<T, C>, value: C)
    fun <C> notEq(column: Column<T, C>, value: C)
    fun <C> gt(column: Column<T, C>, value: C)
    fun <C> ge(column: Column<T, C>, value: C)
    fun <C> lt(column: Column<T, C>, value: C)
    fun <C> le(column: Column<T, C>, value: C)
    fun <C> between(column: Column<T, C>, low: C, high: C)
    fun <C> like(column: Column<T, C>, pattern: String)
    fun <C> isNull(column: Column<T, C>)
    fun <C> isNotNull(column: Column<T, C>)

    fun and(predicate: InnerAdder<T>.() -> Any?)
    fun or(predicate: InnerAdder<T>.() -> Any?)
}