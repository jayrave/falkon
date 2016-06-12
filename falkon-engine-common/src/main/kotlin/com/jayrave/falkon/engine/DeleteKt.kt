package com.jayrave.falkon.engine

/**
 * @return a SQL statement built from the parts passed in
 */
fun buildDeleteSqlFromParts(tableName: String, whereSections: Iterable<WhereSection>?): String {
    // Add basic delete stuff
    val updateSql = StringBuilder(120)
    updateSql.append("DELETE FROM $tableName")

    // Add where clause if required
    val whereSql = whereSections?.buildWhereClause()
    if (whereSql != null) {
        updateSql.append(" $whereSql")
    }

    return updateSql.toString()
}