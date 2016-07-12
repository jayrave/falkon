package com.jayrave.falkon.dao.insert

interface Insert {

    /**
     * the SQL statement this Insert represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}