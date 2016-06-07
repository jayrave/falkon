package com.jayrave.falkon.engine

/**
 * All implementations must be able to handle the following data types natively in addition to
 * `null`
 *
 *      - [Short]
 *      - [Int]
 *      - [Long]
 *      - [Float]
 *      - [Double]
 *      - [String]
 *      - [ByteArray]
 *
 * **Type Handling: ** implementations could choose to handle objects in one of the following
 * two ways
 *
 *      - Don't mangle any objects sent to the engine
 *      - If engine is mangling objects, it should be sophisticated enough to pick out those
 *      that are included in raw DML statements and mangle them also
 */
interface Engine<S : Sink> {

    /**
     * The [Factory] that creates the [Sink]s for this engine
     */
    val sinkFactory: Factory<S>


    /**
     * All changes and queries appear to be Atomic, Consistent, Isolated, and Durable (ACID)
     * when executed inside a transaction
     */
    fun <R> executeInTransaction(operation: () -> R): R?


    /**
     * [tableName] the table to insert into
     * [sink] the sink that contains the initial values for the columns
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun insert(tableName: String, sink: S): Long


    /**
     * [tableName] the table to update
     * [sink] the sink that contains the new values for the columns to be updated
     * [whereClause] the optional WHERE clause (excluding the keyword WHERE). Passing null will
     * update all rows
     * [whereArgs] You may include ?s in the where clause, which will be replaced by the values
     * from whereArgs. Handling of non-native data types is implementation dependent
     *
     * @return the number of rows affected
     */
    fun update(tableName: String, sink: S, whereClause: String?, whereArgs: Iterable<Any?>?): Int


    /**
     * [tableName] the table to delete from
     * [whereClause] the optional WHERE clause (excluding the keyword WHERE). Passing null will
     * delete all rows
     * [whereArgs] You may include ?s in the where clause, which will be replaced by the values
     * from whereArgs. Handling of non-native data types is implementation dependent
     *
     * @return the number of rows affected
     */
    fun delete(tableName: String, whereClause: String?, whereArgs: Iterable<Any?>?): Int


    /**
     * [tableName] the table to delete from
     * [distinct] `true` if you want each row to be unique, `false` otherwise
     * [columns] A list of which columns to return. Passing null will return all columns
     * [whereClause] the optional WHERE clause (excluding the keyword WHERE)
     * [whereArgs] You may include ?s in the where clause, which will be replaced by the values
     * from whereArgs. Handling of non-native data types is implementation dependent
     * [groupBy] A list of columns to SQL GROUP BY clause. Passing null will skip grouping
     * [having] Formatted as SQL HAVING clause (excluding `HAVING` itself). Optional
     * [orderBy] A list of columns & flags to SQL ORDER BY clause. Passing null will skip
     * ordering. When [orderBy] is true ASCENDING order is used; otherwise DESCENDING is used
     * [limit] Limits the number of rows returned by the query, formatted as LIMIT clause.
     * Passing null denotes no limit
     * [offset] Skips the requested number of rows from the beginning and then forms the result
     * set. Passing null denotes no offset
     *
     * @return the source that contains the rows matching the query
     */
    fun query(
            tableName: String, distinct: Boolean, columns: Iterable<String>?, whereClause: String?,
            whereArgs: Iterable<Any?>?, groupBy: Iterable<String>?, having: String?,
            orderBy: Iterable<Pair<String, Boolean>>?, limit: Long?, offset: Long?
    ): Source


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
    fun compileUpdate(rawSql: String): CompiledInsert

    /**
     * [rawSql] raw DELETE statement
     */
    fun compileDelete(rawSql: String): CompiledInsert

    /**
     * [rawSql] raw SELECT statement
     */
    fun compileQuery(rawSql: String): CompiledInsert

    // --------------------------------- Compile raw statements ------------------------------------


    // --------------------------------- Execute raw statements ------------------------------------

    /**
     * Execute raw SQL statement
     */
    fun executeSql(rawSql: String)

    /**
     * [rawSql] raw INSERT statement
     * @return number of rows inserted
     */
    fun executeInsert(rawSql: String): Int

    /**
     * [rawSql] raw UPDATE statement
     * @return number of rows updated
     */
    fun executeUpdate(rawSql: String): Int

    /**
     * [rawSql] raw DELETE statement
     * @return number of rows deleted
     */
    fun executeDelete(rawSql: String): Int

    /**
     * [rawSql] raw SELECT statement
     * @return the source that contains the rows matching the query
     */
    fun executeQuery(rawSql: String): Source

    // --------------------------------- Execute raw statements ------------------------------------
}