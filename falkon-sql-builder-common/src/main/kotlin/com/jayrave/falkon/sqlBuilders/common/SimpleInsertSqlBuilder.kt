package com.jayrave.falkon.sqlBuilders.common

object SimpleInsertSqlBuilder {

    /**
     * Builds a `INSERT INTO ...` statement with the passed in info. Columns are specified
     * in the iteration order of [columns]
     */
    fun build(tableName: String, columns: Iterable<String>): String {
        return SimpleInsertAndCousinsSqlBuilder.build("INSERT", tableName, columns)
    }
}