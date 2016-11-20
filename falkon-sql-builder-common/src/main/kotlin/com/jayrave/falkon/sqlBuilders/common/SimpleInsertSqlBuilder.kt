package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.iterables.IterablesBackedIterable
import com.jayrave.falkon.sqlBuilders.lib.SqlAndIndexToIndicesMap
import java.sql.SQLSyntaxErrorException

object SimpleInsertSqlBuilder {

    /**
     * Builds a `INSERT INTO ...` statement with the passed in info. Columns are specified
     * in the iteration order of [columns]
     */
    fun build(tableName: String, columns: Iterable<String>): String {
        return build("INSERT", tableName, columns)
    }


    /**
     * [phraseForInsertOrReplace] is used to replace the `INSERT` in `INSERT INTO ...`.
     * Columns are specified in the iteration order of [idColumns] & [nonIdColumns]
     */
    fun buildInsertOrReplace(
            phraseForInsertOrReplace: String, tableName: String,
            idColumns: Iterable<String>, nonIdColumns: Iterable<String>):
            SqlAndIndexToIndicesMap {

        if (idColumns.size() == 0) {
            throw SQLSyntaxErrorException("ID columns can't be empty for insert or replace")
        }

        if (nonIdColumns.size() == 0) {
            throw SQLSyntaxErrorException("Non id columns can't be empty for insert or replace")
        }

        val allColumns = IterablesBackedIterable(listOf(idColumns, nonIdColumns))
        return SqlAndIndexToIndicesMap(
                build(phraseForInsertOrReplace, tableName, allColumns),
                SimpleIndexToIndicesMap(allColumns.count())
        )
    }


    private fun build(
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


    private fun Iterable<*>.size(): Int {
        return when (this) {
            is Collection -> size
            else -> count()
        }
    }
}