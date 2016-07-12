package com.jayrave.falkon.dao.update

interface Update {

    /**
     * the SQL statement this Update represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}