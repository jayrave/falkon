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
     *
     * @return number of rows inserted [0, [Int.MAX_VALUE]]
     */
    fun compileInsert(tableName: String, rawSql: String): CompiledStatement<Int>

    /**
     * @param tableName name of table this [rawSql] is concerned with
     * @param rawSql raw UPDATE statement
     *
     * @return number of rows updated [0, [Int.MAX_VALUE]]
     */
    fun compileUpdate(tableName: String, rawSql: String): CompiledStatement<Int>

    /**
     * @param tableName name of table this [rawSql] is concerned with
     * @param rawSql raw DELETE statement
     *
     * @return number of rows deleted [0, [Int.MAX_VALUE]]
     */
    fun compileDelete(tableName: String, rawSql: String): CompiledStatement<Int>

    /**
     * @param tableName name of table this [rawSql] is concerned with
     * @param rawSql raw statement that performs insert or replace
     *
     * @return number of rows inserted or replaced [0, [Int.MAX_VALUE]]
     */
    fun compileInsertOrReplace(tableName: String, rawSql: String): CompiledStatement<Int>

    /**
     * @param tableNames names of tables this [rawSql] is concerned with
     * @param rawSql raw SELECT statement
     *
     * @return source that contains the data produced by the query
     */
    fun compileQuery(tableNames: Iterable<String>, rawSql: String): CompiledStatement<Source>

    /**
     * If the listener is already registered, this is a no-op. This can be called from
     * any thread. Fired [DbEvent]s will be delivered to [DbEventListener]s on the same
     * thread they get fired in. Events fired inside a transaction will be buffered
     * & delivered together if the transaction completes successfully
     */
    fun registerDbEventListener(dbEventListener: DbEventListener)

    /**
     * If the listener isn't already unregistered, this is a no-op. This can be
     * called from any thread
     */
    fun unregisterDbEventListener(dbEventListener: DbEventListener)
}