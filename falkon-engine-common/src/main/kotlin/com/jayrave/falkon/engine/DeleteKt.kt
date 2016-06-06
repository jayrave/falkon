package com.jayrave.falkon.engine

/**
 * @return a compiled statement for the passed in parameters
 */
inline fun <CS : Any> compileDeleteStatement(
        tableName: String, whereClause: String?,
        whereArgs: Iterable<Any?>?, statementCompiler: (String) -> CS,
        argsBinder: (compiledStatement: CS, index: Int, arg: Any?) -> Any?): CS {

    // Add basic delete stuff
    val updateSql = StringBuilder(120)
    updateSql.append("DELETE FROM $tableName")

    // Add where clause if required
    if (!whereClause.isNullOrBlank()) {
        updateSql.append(" WHERE $whereClause")
    }

    // Bind where args if required
    val compiledStatement = statementCompiler.invoke(updateSql.toString())
    whereArgs?.forEachIndexed { index, arg ->
        argsBinder.invoke(compiledStatement, index, arg)
    }

    return compiledStatement
}