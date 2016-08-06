package com.jayrave.falkon.engine

/**
 * Exposes methods to interface with the database
 */
interface Engine {

    /**
     * All changes made to the database inside a transaction will be ACID (Atomic, Consistent,
     * Isolated, and Durable). Behaviour of database resources passed passed outside of
     * transaction is implementation dependent. Examples of such resources are
     * [CompiledStatement], [Source] etc.
     *
     * Transactions can be nested. When transactions are nested, the following rules hold
     *
     *      - Only when the outermost transaction is committed, the changes will be persisted
     *      - If any of the transactions fail, the database will be restored to the state that
     *      was before the outermost transaction started
     */
    fun <R> executeInTransaction(operation: () -> R): R

    /**
     * @return `true` if there is an active transaction in the current thread
     */
    fun isInTransaction(): Boolean

    /**
     * @param tableNames names of tables this [rawSql] is concerned with
     * @param rawSql raw SQL statement
     */
    fun compileSql(tableNames: Iterable<String>?, rawSql: String): CompiledStatement<Unit>

    /**
     * @param tableName name of table this [rawSql] is concerned with
     * @param rawSql raw INSERT statement
     */
    fun compileInsert(tableName: String, rawSql: String): CompiledInsert

    /**
     * @param tableName name of table this [rawSql] is concerned with
     * @param rawSql raw UPDATE statement
     */
    fun compileUpdate(tableName: String, rawSql: String): CompiledUpdate

    /**
     * @param tableName name of table this [rawSql] is concerned with
     * @param rawSql raw DELETE statement
     */
    fun compileDelete(tableName: String, rawSql: String): CompiledDelete

    /**
     * @param tableNames names of tables this [rawSql] is concerned with
     * @param rawSql raw SELECT statement
     */
    fun compileQuery(tableNames: Iterable<String>, rawSql: String): CompiledQuery
}