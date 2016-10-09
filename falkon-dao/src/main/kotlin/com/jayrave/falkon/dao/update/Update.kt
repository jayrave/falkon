package com.jayrave.falkon.dao.update

/**
 * Carries information about `UPDATE...` statements
 */
interface Update {

    /**
     * table this update statement corresponds to
     */
    val tableName: String

    /**
     * the SQL statement this Update represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}