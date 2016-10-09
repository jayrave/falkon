package com.jayrave.falkon.dao.query

/**
 * Carries information about `SELECT...` statements
 */
interface Query {

    /**
     * tables this query corresponds to
     */
    val tableNames: Iterable<String>

    /**
     * the SQL statement this Query represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}