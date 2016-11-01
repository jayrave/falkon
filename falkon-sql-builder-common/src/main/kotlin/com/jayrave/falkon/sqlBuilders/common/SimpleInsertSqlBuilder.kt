package com.jayrave.falkon.sqlBuilders.common

import java.sql.SQLSyntaxErrorException

/**
 * To build `insert or replace` statement `INSERT` will be replace by the string returned by
 * [phraseForInsertOrReplace]
 */
class SimpleInsertSqlBuilder(private val phraseForInsertOrReplace: () -> String) {

    private val phraseForInsert = { "INSERT" }

    fun build(tableName: String, columns: Iterable<String>, argPlaceholder: String): String {
        return build(phraseForInsert, tableName, columns, argPlaceholder)
    }

    fun buildInsertOrReplace(
            tableName: String, columns: Iterable<String>, argPlaceholder: String): String {
        return build(phraseForInsertOrReplace, tableName, columns, argPlaceholder)
    }


    companion object {
        private fun build(
                phraseForInsertAndCousins: () -> String, tableName: String,
                columns: Iterable<String>, argPlaceholder: String): String {

            // Add basic insert stuff
            val sql = StringBuilder(120)
            sql
                    .append(phraseForInsertAndCousins.invoke())
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
}