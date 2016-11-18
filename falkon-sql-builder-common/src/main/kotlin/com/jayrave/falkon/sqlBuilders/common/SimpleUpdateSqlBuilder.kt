package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import java.sql.SQLSyntaxErrorException

object SimpleUpdateSqlBuilder {

    /**
     * Builds a `UPDATE ...` statement with the passed in info. Conditions for `WHERE`
     * clause are added in the iteration order of [whereSections]
     */
    fun build(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?):
            String {

        // Add basic update stuff
        val updateSql = StringBuilder(120)
        updateSql.append("UPDATE $tableName SET ")

        // Add column names & their value placeholders
        var columnCount = 0
        updateSql.append(columns.joinToString(separator = ", ") {
            columnCount++
            "$it = $ARG_PLACEHOLDER"
        })

        when (columnCount) {
            0 -> throw SQLSyntaxErrorException(
                    "UPDATE SQL without any columns for table: $tableName"
            )

            else -> {
                // Add where clause if required
                val whereSql = whereSections?.buildWhereClause(ARG_PLACEHOLDER)
                if (whereSql != null) {
                    updateSql.append(" $whereSql")
                }
            }
        }

        return updateSql.toString()
    }
}