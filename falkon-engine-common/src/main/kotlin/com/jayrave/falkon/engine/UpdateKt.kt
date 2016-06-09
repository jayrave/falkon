package com.jayrave.falkon.engine

import java.util.*

/**
 * @return a compiled statement for the passed in parameters if it has the potential
 * to directly update some rows; else null
 */
inline fun <CS : Any> compileUpdateStatement(
        tableName: String, columnNamesToValuesMap: Map<String, Any?>, whereClause: String?,
        whereArgs: Iterable<Any?>?, statementCompiler: (String) -> CS,
        argsBinder: (compiledStatement: CS, index: Int, arg: Any?) -> Any?): CS? {

    return when {
        columnNamesToValuesMap.isEmpty() -> null
        else -> {
            val updateSql = StringBuilder(120)
            updateSql.append("UPDATE $tableName SET ")

            val valuesForColumns = ArrayList<Any?>(columnNamesToValuesMap.size)
            updateSql.append(columnNamesToValuesMap.entries.joinToString(separator = ", ") {
                valuesForColumns.add(it.value)
                "${it.key} = ?"
            })

            if (!whereClause.isNullOrBlank()) {
                updateSql.append(" WHERE $whereClause")
            }

            val compiledStatement = statementCompiler.invoke(updateSql.toString())
            valuesForColumns.forEachIndexed { index, value ->
                argsBinder.invoke(compiledStatement, index, value)
            }

            whereArgs?.forEachIndexed { index, arg ->
                argsBinder.invoke(compiledStatement, valuesForColumns.size + index, arg)
            }

            return compiledStatement
        }
    }
}