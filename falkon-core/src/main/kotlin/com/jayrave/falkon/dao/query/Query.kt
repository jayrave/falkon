package com.jayrave.falkon.dao.query

/**
 * @param sql the SQL statement this Query represents
 * @param arguments contains arguments for placeholders used in sections
 */
data class Query(val sql: String, val arguments: Iterable<Any>?)