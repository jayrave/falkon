package com.jayrave.falkon.sqlBuilders.lib

/**
 * If [alias] is `null`, [columnName] will be used straight up in the SQL. Otherwise, the
 * column name will be aliased with [alias]
 */
interface SelectColumnInfo {
    val columnName: String
    val alias: String?
}