package com.jayrave.falkon.engine

/**
 * @return a SQL statement built from the parts passed in
 */
fun buildDeleteSqlFromParts(
        tableName: String, whereSections: Iterable<WhereSection>?,
        argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String {

    // Add basic delete stuff
    val deleteSql = StringBuilder(120)
    deleteSql.append("DELETE FROM $tableName")

    // Add where clause if required
    val whereSql = whereSections?.buildWhereClause(argPlaceholder)
    if (whereSql != null) {
        deleteSql.append(" $whereSql")
    }

    return deleteSql.toString()
}