package com.jayrave.falkon.dao.insertOrReplace

/**
 * Carries information about insert or replace statement
 */
interface InsertOrReplace {

    /**
     * table this statement inserts or replaces into
     */
    val tableName: String

    /**
     * the SQL statement this represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}