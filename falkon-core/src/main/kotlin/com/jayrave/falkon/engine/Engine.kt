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
     * [tableName] the table to delete from
     * [sink] the sink that contains the initial values for the columns
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun insert(tableName: String, sink: S): Long

    /**
     * [tableName] the table to delete from
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
}