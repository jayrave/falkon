package com.jayrave.falkon.sqlBuilders.common

import java.sql.SQLSyntaxErrorException

object SimpleInsertSqlBuilder {

    /**
     * Builds a `INSERT INTO ...` statement with the passed in info. Columns are specified
     * in the iteration order of [columns]
     */
    fun build(tableName: String, columns: Iterable<String>, argPlaceholder: String): String {
        return build("INSERT", tableName, columns, argPlaceholder)
    }

    /**
     * Builds a statement akin to `INSERT OR REPLACE INTO ...`. Columns are specified
     * in the iteration order of [columns]. [phraseForInsertOrReplace] is used to replace
     * the `INSERT` from `INSERT INTO ...`
     */
    fun buildInsertOrReplace(
            phraseForInsertOrReplace: String, tableName: String,
            columns: Iterable<String>, argPlaceholder: String): String {

        return build(phraseForInsertOrReplace, tableName, columns, argPlaceholder)
    }


    private fun build(
            phraseForInsertAndCousins: String, tableName: String,
            columns: Iterable<String>, argPlaceholder: String): String {

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
                sql.append((0..columnCount - 1).joinToString(
                        separator = ", ", prefix = "(", postfix = ")") {
                    argPlaceholder
                })

                sql.toString()
            }
        }
    }
}