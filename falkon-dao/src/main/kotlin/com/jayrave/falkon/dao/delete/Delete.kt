package com.jayrave.falkon.dao.delete

/**
 * Carries information about `DELETE FROM...` statements
 */
interface Delete {

    /**
     * table this delete statement corresponds to
     */
    val tableName: String

    /**
     * the SQL statement this Delete represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}