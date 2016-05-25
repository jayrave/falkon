package com.jayrave.falkon.dao.where

import com.jayrave.falkon.Column

/**
 * Interface which can be used to build the WHERE clause of a SQL statement
 */
interface WhereBuilder<T : Any, ID : Any> :
        PredicateAdder<T, ID>,
        CompoundConnectorAdder<T, ID>


/**
 * Interface that exposes methods which will add a predicate to the WHERE clause of
 * a SQL statement
 */
interface PredicateAdder<T : Any, ID : Any> {
    fun <C> eq(column: Column<T, C>, value: C): AdderOrEnder<T, ID>
    fun <C> notEq(column: Column<T, C>, value: C): AdderOrEnder<T, ID>
    fun <C> gt(column: Column<T, C>, value: C): AdderOrEnder<T, ID>
    fun <C> ge(column: Column<T, C>, value: C): AdderOrEnder<T, ID>
    fun <C> lt(column: Column<T, C>, value: C): AdderOrEnder<T, ID>
    fun <C> le(column: Column<T, C>, value: C): AdderOrEnder<T, ID>
    fun <C> between(column: Column<T, C>, low: C, high: C): AdderOrEnder<T, ID>
    fun <C> like(column: Column<T, C>, pattern: String): AdderOrEnder<T, ID>
    fun <C> isNull(column: Column<T, C>): AdderOrEnder<T, ID>
    fun <C> isNotNull(column: Column<T, C>): AdderOrEnder<T, ID>
}


/**
 * Interface that exposes methods to either add AND or OR between predicates
 */
interface SimpleConnectorAdder<T : Any, ID : Any> {
    fun and(): AfterSimpleConnectorAdder<T, ID>
    fun or(): AfterSimpleConnectorAdder<T, ID>
}


/**
 * Interface that exposes methods to group predicates and combine them
 */
interface CompoundConnectorAdder<T : Any, ID : Any> {
    fun and(predicate: InnerAdder<T, ID>.() -> Any?): AdderOrEnder<T, ID>
    fun or(predicate: InnerAdder<T, ID>.() -> Any?): AdderOrEnder<T, ID>
}


/**
 * Just a semantic extension of [SimpleConnectorAdder] that doesn't add any other method. This most
 * probably will be extended where [WhereBuilder] needs to be extended
 */
interface AdderOrEnder<T : Any, ID : Any> : SimpleConnectorAdder<T, ID>


/**
 * Just a combination of [PredicateAdder] & [CompoundConnectorAdder] which gets returned
 * by methods of [SimpleConnectorAdder] to allow adding more predicates
 */
interface AfterSimpleConnectorAdder<T : Any, ID : Any> :
        PredicateAdder<T, ID>,
        CompoundConnectorAdder<T, ID>


/**
 * The interface that will be exposed inside function objects passed to methods in [CompoundConnectorAdder]
 * to add more predicates to the WHERE clause
 */
interface InnerAdder<T : Any, ID : Any> {
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

    fun and(predicate: InnerAdder<T, ID>.() -> Any?)
    fun or(predicate: InnerAdder<T, ID>.() -> Any?)
}