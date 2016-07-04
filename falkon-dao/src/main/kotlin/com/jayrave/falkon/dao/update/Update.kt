package com.jayrave.falkon.dao.update

/**
 * @param sql the SQL statement this Update represents
 * @param arguments contains arguments for placeholders used in sections
 */
data class Update(val sql: String, val arguments: Iterable<Any>?)