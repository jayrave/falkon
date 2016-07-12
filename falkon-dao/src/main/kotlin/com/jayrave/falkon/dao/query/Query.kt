package com.jayrave.falkon.dao.query

interface Query {

    /**
     * the SQL statement this Query represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}