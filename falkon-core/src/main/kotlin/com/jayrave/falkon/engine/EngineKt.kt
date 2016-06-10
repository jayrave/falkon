package com.jayrave.falkon.engine

// ------------------------------------- Compile from parts ----------------------------------------

/**
 * [tableName] the table to insert into
 * [columns] A list of columns for which values will be bound later
 */
fun Engine.compileInsert(tableName: String, columns: Iterable<String>): CompiledInsert {
    return compileInsert(buildInsertSql(tableName, columns))
}

/**
 * [tableName] the table to update
 * [columns] A list of columns for which values will be bound later
 * [whereClause] the optional WHERE clause (excluding the keyword WHERE). Passing null will
 * update all rows
 */
fun Engine.compileUpdate(tableName: String, columns: Iterable<String>, whereClause: String?):
        CompiledUpdate {
    return compileUpdate(buildUpdateSql(tableName, columns, whereClause))
}

/**
 * [tableName] the table to delete from
 * [whereClause] the optional WHERE clause (excluding the keyword WHERE). Passing null will
 * delete all rows
 */
fun Engine.compileDelete(tableName: String, whereClause: String?): CompiledDelete {
    return compileDelete(buildDeleteSql(tableName, whereClause))
}

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
fun Engine.compileQuery(
        tableName: String, distinct: Boolean, columns: Iterable<String>?, whereClause: String?,
        groupBy: Iterable<String>?, having: String?, orderBy: Iterable<Pair<String, Boolean>>?,
        limit: Long?, offset: Long?): CompiledQuery {

    return compileQuery(buildQuerySql(
            tableName, distinct, columns, whereClause, groupBy,
            having, orderBy, limit, offset
    ))
}

// ------------------------------------- Compile from parts ----------------------------------------


// -------------------------------------- Execute raw SQL ------------------------------------------

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 */
fun Engine.executeSql(rawSql: String, bindArgs: Iterable<Any?>? = null) {
    compileSql(rawSql).bindAll(bindArgs).execute()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return number of rows inserted
 */
fun Engine.executeInsert(rawSql: String, bindArgs: Iterable<Any?>? = null): Int {
    return compileInsert(rawSql).bindAll(bindArgs).execute()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return number of rows updated
 */
fun Engine.executeUpdate(rawSql: String, bindArgs: Iterable<Any?>? = null): Int {
    return compileUpdate(rawSql).bindAll(bindArgs).execute()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return number of rows deleted
 */
fun Engine.executeDelete(rawSql: String, bindArgs: Iterable<Any?>? = null): Int {
    return compileDelete(rawSql).bindAll(bindArgs).execute()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return the source that contains the rows matching the query
 */
fun Engine.executeQuery(rawSql: String, bindArgs: Iterable<Any?>? = null): Source {
    return compileQuery(rawSql).bindAll(bindArgs).execute()
}

// -------------------------------------- Execute raw SQL ------------------------------------------