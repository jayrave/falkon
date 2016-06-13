package com.jayrave.falkon.engine

/**
 * @return a SQL statement built from the parts passed in if at least one column name is
 * passed in else `null` is returned
 */
fun buildInsertSqlFromParts(
        tableName: String, columns: Iterable<String>,
        argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String? {

    // Add basic insert stuff
    val insertSql = StringBuilder(120)
    insertSql.append("INSERT INTO $tableName ")

    // Add column names
    var columnCount = 0
    insertSql.append(columns.joinToString(separator = ", ", prefix = "(", postfix = ")") {
        columnCount++
        it
    })

    // Add required placeholders (return null if there are no columns to set value to)
    return when (columnCount) {
        0 -> null
        else -> {
            insertSql.append(" VALUES ")
            insertSql.append((0..columnCount - 1).joinToString(
                    separator = ", ", prefix = "(", postfix = ")") {
                argPlaceholder
            })

            insertSql.toString()
        }
    }
}