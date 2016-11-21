package com.jayrave.falkon.sqlBuilders

interface InsertSqlBuilder {

    /**
     * Builds a db specific statement similar to `INSERT INTO ...`
     *
     * @param [tableName] the table to insert into
     * @param [columns] a list of columns (applied in iteration order) for which values will
     * be bound later
     */
    fun build(tableName: String, columns: Iterable<String>): String
}