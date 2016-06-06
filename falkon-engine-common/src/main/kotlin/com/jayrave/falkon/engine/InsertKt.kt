package com.jayrave.falkon.engine

import java.util.*

/**
 * @see compileInsertStatement
 */
inline fun <CS : Any> compileInsertStatement(
        tableName: String, sink: MapBackedSink, statementCompiler: (String) -> CS,
        argsBinder: (compiledStatement: CS, index: Int, arg: Any?) -> Any?): CS? {

    return compileInsertStatement(tableName, sink.map, statementCompiler, argsBinder)
}


/**
 * @return a compiled statement for the passed in parameters if it has the potential
 * to directly insert some rows; else null
 */
inline fun <CS : Any> compileInsertStatement(
        tableName: String, columnNamesToValuesMap: Map<String, Any?>,
        statementCompiler: (String) -> CS,
        argsBinder: (compiledStatement: CS, index: Int, arg: Any?) -> Any?): CS? {

    return when {
        columnNamesToValuesMap.isEmpty() -> null
        else -> {
            val insertSql = StringBuilder(120)
            insertSql.append("INSERT INTO $tableName ")

            // Add column names section
            val valuesForColumns = ArrayList<Any?>(columnNamesToValuesMap.size)
            insertSql.append(columnNamesToValuesMap.entries.joinToString(
                    separator = ", ", prefix = "(", postfix = ")") {
                valuesForColumns.add(it.value)
                it.key
            })

            // Add placeholders
            insertSql.append(" VALUES ")
            insertSql.append(valuesForColumns.joinToString(
                    separator = ", ", prefix = "(", postfix = ")") {
                "?"
            })

            // Bind column values
            val compiledStatement = statementCompiler.invoke(insertSql.toString())
            valuesForColumns.forEachIndexed { index, value ->
                argsBinder.invoke(compiledStatement, index, value)
            }

            return compiledStatement
        }
    }
}