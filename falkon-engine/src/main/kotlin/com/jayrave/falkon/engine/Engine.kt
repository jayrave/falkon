package com.jayrave.falkon.engine

/**
 * Exposes methods to interface with the database
 */
interface Engine {

    /**
     * All changes and queries when executed inside a transaction will be ACID (Atomic,
     * Consistent, Isolated, and Durable). Behaviour of database resources passed passed
     * outside of transaction is implementation dependent. Examples of such resources
     * are [CompiledStatement], [Source] etc.
     */
    fun <R> executeInTransaction(operation: () -> R): R

    /**
     * @return `true` if there is an active transaction in the current thread (only one
     * transaction can be active per thread at a time)
     */
    fun isInTransaction(): Boolean

    /**
     * Compile raw SQL statement
     */
    fun compileSql(rawSql: String): CompiledStatement<Unit>

    /**
     * [rawSql] raw INSERT statement
     */
    fun compileInsert(rawSql: String): CompiledInsert

    /**
     * [rawSql] raw UPDATE statement
     */
    fun compileUpdate(rawSql: String): CompiledUpdate

    /**
     * [rawSql] raw DELETE statement
     */
    fun compileDelete(rawSql: String): CompiledDelete

    /**
     * [rawSql] raw SELECT statement
     */
    fun compileQuery(rawSql: String): CompiledQuery
}