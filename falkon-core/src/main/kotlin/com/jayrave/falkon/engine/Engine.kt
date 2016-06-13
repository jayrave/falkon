package com.jayrave.falkon.engine

/**
 * Exposes methods to interface with the database
 */
interface Engine {

    /**
     * All changes and queries appear to be Atomic, Consistent, Isolated, and Durable (ACID)
     * when executed inside a transaction
     */
    fun <R> executeInTransaction(operation: () -> R): R?


    // ---------------------------------- Build SQL from parts -------------------------------------

    /**
     * [tableName] the table to insert into
     * [columns] A list of columns for which values will be bound later
     */
    fun buildInsertSql(tableName: String, columns: Iterable<String>): String

    /**
     * [tableName] the table to update
     * [columns] A list of columns for which values will be bound later
     * [whereSections] A list of sections, applied in iteration order used to build the optional
     * SQL WHERE clause. Passing null denotes no WHERE in the built SQL
     */
    fun buildUpdateSql(
            tableName: String, columns: Iterable<String>,
            whereSections: Iterable<WhereSection>?): String

    /**
     * [tableName] the table to delete from
     * [whereSections] A list of sections, applied in iteration order used to build the optional
     * SQL WHERE clause. Passing null denotes no WHERE in the built SQL
     */
    fun buildDeleteSql(tableName: String, whereSections: Iterable<WhereSection>?): String

    /**
     * [tableName] the table to delete from
     * [distinct] `true` if you want each row to be unique, `false` otherwise
     * [columns] A list of which columns to return, applied in iteration order. Passing null will
     * return all columns
     * [whereSections] A list of sections, applied in iteration order used to build the optional
     * SQL WHERE clause. Passing null denotes no WHERE in the built SQL
     * [groupBy] A list of columns to SQL GROUP BY clause applied in iteration order. Passing null
     * will skip grouping
     * [orderBy] A list of [OrderInfo] to SQL ORDER BY clause applied in iteration order.
     * Passing null will skip ordering
     * [limit] Limits the number of rows returned by the query, formatted as LIMIT clause.
     * Passing null denotes no limit
     * [offset] Skips the requested number of rows from the beginning and then forms the result
     * set. Passing null denotes no offset
     */
    fun buildQuerySql(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?): String

    // ---------------------------------- Build SQL from parts -------------------------------------


    // --------------------------------- Compile raw statements ------------------------------------

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

    // --------------------------------- Compile raw statements ------------------------------------
}