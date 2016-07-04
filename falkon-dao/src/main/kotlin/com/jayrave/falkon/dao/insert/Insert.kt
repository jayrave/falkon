package com.jayrave.falkon.dao.insert

/**
 * @param sql the SQL statement this Insert represents
 * @param arguments contains arguments for placeholders used in sections
 */
data class Insert(val sql: String, val arguments: Iterable<Any>?)