package com.jayrave.falkon.engine

/**
 * @return a SQL statement built from the parts passed in if at least one column name is
 * passed in else `null` is returned
 */
fun buildUpdateSqlFromParts(
        tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?,
        argPlaceholder: String = "?"): String? {

    // Add basic update stuff
    val updateSql = StringBuilder(120)
    updateSql.append("UPDATE $tableName SET ")

    // Add column names & their value placeholders
    var columnCount = 0
    updateSql.append(columns.joinToString(separator = ", ") {
        columnCount++
        "$it = $argPlaceholder"
    })

    when (columnCount) {
        0 -> return null // No columns are set. Return null
        else -> {
            // Add where clause if required
            val whereSql = whereSections?.buildWhereClause()
            if (whereSql != null) {
                updateSql.append(" $whereSql")
            }
        }
    }

    return updateSql.toString()
}