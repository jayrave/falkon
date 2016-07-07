package com.jayrave.falkon.engine

/**
 * An #executeQuery convenience function is not included here as it doesn't make sense to.
 * Source that is returned from a CompiledQuery could end up not working if the CompiledQuery
 * itself is closed
 */

/**
 * Convenience method to compile the raw SQL, bind arguments, execute it
 */
fun Engine.executeSql(rawSql: String, bindArgs: Iterable<Any>? = null) {
    compileSql(rawSql)
            .closeIfOpThrows { bindAll(bindArgs) }
            .safeCloseAfterExecution()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return number of rows inserted
 */
fun Engine.executeInsert(rawSql: String, bindArgs: Iterable<Any>? = null): Int {
    return compileInsert(rawSql)
            .closeIfOpThrows { bindAll(bindArgs) }
            .safeCloseAfterExecution()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return number of rows updated
 */
fun Engine.executeUpdate(rawSql: String, bindArgs: Iterable<Any>? = null): Int {
    return compileUpdate(rawSql)
            .closeIfOpThrows { bindAll(bindArgs) }
            .safeCloseAfterExecution()
}

/**
 * Convenience method to compile the raw SQL, bind arguments and execute it
 * @return number of rows deleted
 */
fun Engine.executeDelete(rawSql: String, bindArgs: Iterable<Any>? = null): Int {
    return compileDelete(rawSql)
            .closeIfOpThrows { bindAll(bindArgs) }
            .safeCloseAfterExecution()
}
