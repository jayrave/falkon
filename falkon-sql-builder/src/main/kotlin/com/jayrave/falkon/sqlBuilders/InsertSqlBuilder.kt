package com.jayrave.falkon.sqlBuilders

interface InsertSqlBuilder {
    fun build(
            tableName: String,
            columns: Iterable<String>,
            argPlaceholder: String
    ): String
}