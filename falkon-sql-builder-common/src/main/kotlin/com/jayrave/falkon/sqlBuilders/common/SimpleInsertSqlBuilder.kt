package com.jayrave.falkon.sqlBuilders.common

import java.sql.SQLSyntaxErrorException

class SimpleInsertSqlBuilder {

    fun build(tableName: String, columns: Iterable<String>, argPlaceholder: String): String {
        // Add basic insert stuff
        val insertSql = StringBuilder(120)
        insertSql.append("INSERT INTO $tableName ")

        // Add column names
        var columnCount = 0
        insertSql.append(columns.joinToString(separator = ", ", prefix = "(", postfix = ")") {
            columnCount++
            it
        })

        // Add required placeholders
        return when (columnCount) {
            0 -> throw SQLSyntaxErrorException(
                    "INSERT SQL without any columns for table: $tableName"
            )

            else -> {
                insertSql.append(" VALUES ")
                insertSql.append((0..columnCount - 1).joinToString(
                        separator = ", ", prefix = "(", postfix = ")") {
                    argPlaceholder
                })

                insertSql.toString()
            }
        }
    }
}