package com.jayrave.falkon.dao.delete

interface Delete {

    /**
     * the SQL statement this Delete represents
     */
    val sql: String

    /**
     * contains arguments for placeholders used in [sql]
     */
    val arguments: Iterable<Any>
}