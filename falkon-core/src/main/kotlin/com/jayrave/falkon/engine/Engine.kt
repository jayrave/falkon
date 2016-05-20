package com.jayrave.falkon.engine

/**
 * All implementations must be able to handle the following data types natively in addition to `null`
 *
 *      - [Short]
 *      - [Int]
 *      - [Long]
 *      - [Float]
 *      - [Double]
 *      - [String]
 *      - [ByteArray]
 *
 *      Only the above types are demanded from [Source] and sent to [Sink]. All the raw queries & statements
 * sent to the engine for processing will only contain objects of the above mentioned types.
 *
 * **Type Handling: ** implementations could choose to handle objects in one of the following two ways
 *
 *      - Don't mangle any objects sent to the engine
 *      - If engine is mangling objects, it should be sophisticated enough to pick out those that are included
 *      in raw DML statements and mangle them also
 */
interface Engine<S : Sink> {

    /**
     * The [Factory] that creates the [Sink]s for this engine
     */
    val sinkFactory: Factory<S>

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
     * [whereClause] the optional WHERE clause to apply when updating. Passing null will update all rows
     * [whereArgs] You may include ?s in the where clause, which will be replaced by the values from whereArgs
     *
     * @return the number of rows affected
     */
    fun update(tableName: String, sink: S, whereClause: String, whereArgs: Array<Any?>): Int

    /**
     * [tableName] the table to delete from
     * [whereClause] the optional WHERE clause to apply when deleting. Passing null will delete all rows
     * [whereArgs] You may include ?s in the where clause, which will be replaced by the values from whereArgs
     *
     * @return the number of rows affected
     */
    fun delete(tableName: String, whereClause: String, whereArgs: Array<Any?>): Int

    /**
     * [tableName] the table to delete from
     * [distinct] `true` if you want each row to be unique, `false` otherwise
     * [columns] A list of which columns to return. Passing null will return all columns
     * [whereClause] the optional WHERE clause to apply when updating. Passing null will update all rows
     * [whereArgs] You may include ?s in the where clause, which will be replaced by the values from whereArgs
     * [groupBy] Formatted as SQL GROUP BY clause (excluding `GROUP BY` itself). Passing null will skip grouping
     * [having] Formatted as SQL HAVING clause (excluding `HAVING` itself). Passing null will skip grouping
     * [orderBy] Formatted as SQL ORDER BY clause (excluding `ORDER BY` itself). Passing null will skip ordering
     * [limit] Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no limit
     *
     * @return the source that contains the rows matching the query
     */
    fun query(
            tableName: String, distinct: Boolean?, columns: Array<String>?, whereClause: String?,
            whereArgs: Array<Any?>, groupBy: String?, having: String?, orderBy: String?, limit: String?
    ): Source
}