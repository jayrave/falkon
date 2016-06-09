package com.jayrave.falkon.engine

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