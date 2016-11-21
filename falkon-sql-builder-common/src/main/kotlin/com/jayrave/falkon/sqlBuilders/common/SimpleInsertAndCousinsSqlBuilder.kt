package com.jayrave.falkon.sqlBuilders.common

import java.sql.SQLSyntaxErrorException

internal object SimpleInsertAndCousinsSqlBuilder {

    /**
     * Builds a `INSERT INTO ...` statement with the passed in info but with the `INSERT`
     * replaced by [phraseForInsertAndCousins]. Columns are specified in the iteration
     * order of [columns]
     */
    internal fun build(
            phraseForInsertAndCousins: String, tableName: String,
            columns: Iterable<String>): String {

        // Add basic insert stuff
        val sql = StringBuilder(120)
        sql
                .append(phraseForInsertAndCousins)
                .append(" INTO $tableName ")

        // Add column names
        var columnCount = 0
        sql.append(columns.joinToString(separator = ", ", prefix = "(", postfix = ")") {
            columnCount++
            it
        })

        // Add required placeholders
        return when (columnCount) {
            0 -> throw SQLSyntaxErrorException(
                    "INSERT SQL without any columns for table: $tableName"
            )

            else -> {
                sql.append(" VALUES ")
                sql.append((0..columnCount - 1).joinToString(prefix = "(", postfix = ")") {
                    ARG_PLACEHOLDER
                })

                sql.toString()
            }
        }
    }
}