package com.jayrave.falkon.engine

// ------------------------------------- Compile from parts ----------------------------------------

/**
 * Convenience method to compile an SQL INSERT from its parts
 */
fun Engine.compileInsert(tableName: String, columns: Iterable<String>): CompiledInsert {
    return compileInsert(buildInsertSql(tableName, columns))
}

/**
 * Convenience method to compile an SQL UPDATE from its parts
 */
fun Engine.compileUpdate(
        tableName: String, columns: Iterable<String>,
        whereSections: Iterable<WhereSection>?): CompiledUpdate {

    return compileUpdate(buildUpdateSql(tableName, columns, whereSections))
}

/**
 * Convenience method to compile an SQL DELETE from its parts
 */
fun Engine.compileDelete(
        tableName: String, whereSections: Iterable<WhereSection>?): CompiledDelete {

    return compileDelete(buildDeleteSql(tableName, whereSections))
}

/**
 * Convenience method to compile an SQL SELECT from its parts
 */
fun Engine.compileQuery(
        tableName: String, distinct: Boolean, columns: Iterable<String>?,
        whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
        orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?): CompiledQuery {

    return compileQuery(buildQuerySql(
            tableName, distinct, columns, whereSections, groupBy, orderBy, limit, offset
    ))
}

// ------------------------------------- Compile from parts ----------------------------------------


// -------------------------------------- Execute raw SQL ------------------------------------------

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


// An #executeQuery convenience function is not included here as it doesn't make sense to.
// Source that is returned from a CompiledQuery could end up not working if the CompiledQuery
// itself is closed

// -------------------------------------- Execute raw SQL ------------------------------------------