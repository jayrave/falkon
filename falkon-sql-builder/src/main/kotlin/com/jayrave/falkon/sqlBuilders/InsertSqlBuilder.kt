package com.jayrave.falkon.sqlBuilders

interface InsertSqlBuilder {

    /**
     * @param [tableName] the table to insert into
     * @param [columns] a list of columns for which values will be bound later
     * @param [argPlaceholder] to use as placeholders to prevent SQL injection
     */
    fun build(
            tableName: String,
            columns: Iterable<String>,
            argPlaceholder: String
    ): String
}