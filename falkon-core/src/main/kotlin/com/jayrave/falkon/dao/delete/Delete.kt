package com.jayrave.falkon.dao.delete

/**
 * @param sql the SQL statement this Delete represents
 * @param arguments contains arguments for placeholders used in sections
 */
data class Delete(val sql: String, val arguments: Iterable<Any>?)