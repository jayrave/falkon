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


    // ----------------------------------- Compile from parts --------------------------------------

    /**
     * [tableName] the table to insert into
     * [columns] A list of columns for which values will be bound later
     */
    fun compileInsert(tableName: String, columns: Iterable<String>): CompiledInsert

    /**
     * [tableName] the table to update
     * [columns] A list of columns for which values will be bound later
     * [whereClause] the optional WHERE clause (excluding the keyword WHERE). Passing null will
     * update all rows
     */
    fun compileUpdate(tableName: String, columns: Iterable<String>, whereClause: String?):
            CompiledUpdate

    /**
     * [tableName] the table to delete from
     * [whereClause] the optional WHERE clause (excluding the keyword WHERE). Passing null will
     * delete all rows
     */
    fun compileDelete(tableName: String, whereClause: String?): CompiledDelete

    /**
     * [tableName] the table to delete from
     * [distinct] `true` if you want each row to be unique, `false` otherwise
     * [columns] A list of which columns to return. Passing null will return all columns
     * [whereClause] the optional WHERE clause (excluding the keyword WHERE)
     * [groupBy] A list of columns to SQL GROUP BY clause. Passing null will skip grouping
     * [having] Formatted as SQL HAVING clause (excluding `HAVING` itself). Optional
     * [orderBy] A list of columns & flags to SQL ORDER BY clause. Passing null will skip
     * ordering. When [orderBy] is true ASCENDING order is used; otherwise DESCENDING is used
     * [limit] Limits the number of rows returned by the query, formatted as LIMIT clause.
     * Passing null denotes no limit
     * [offset] Skips the requested number of rows from the beginning and then forms the result
     * set. Passing null denotes no offset
     */
    fun compileQuery(
            tableName: String, distinct: Boolean, columns: Iterable<String>?, whereClause: String?,
            groupBy: Iterable<String>?, having: String?, orderBy: Iterable<Pair<String, Boolean>>?,
            limit: Long?, offset: Long?): CompiledQuery


    // ----------------------------------- Compile from parts --------------------------------------


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