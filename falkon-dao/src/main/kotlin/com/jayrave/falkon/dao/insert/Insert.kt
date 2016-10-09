package com.jayrave.falkon.dao.insert

/**
 * Carries information about `INSERT INTO...` statements
 */
interface Insert {

    /**
     * table this insert statement corresponds to
     */
    val tableName: String

    /**
     * the SQL statement this Insert represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}