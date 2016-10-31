package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import java.sql.SQLSyntaxErrorException

class SimpleUpdateSqlBuilder : UpdateSqlBuilder {

    override fun build(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String): String {

        // Add basic update stuff
        val updateSql = StringBuilder(120)
        updateSql.append("UPDATE $tableName SET ")

        // Add column names & their value placeholders
        var columnCount = 0
        updateSql.append(columns.joinToString(separator = ", ") {
            columnCount++
            "$it = $argPlaceholder"
        })

        when (columnCount) {
            0 -> throw SQLSyntaxErrorException(
                    "UPDATE SQL without any columns for table: $tableName"
            )

            else -> {
                // Add where clause if required
                val whereSql = whereSections?.buildWhereClause(argPlaceholder)
                if (whereSql != null) {
                    updateSql.append(" $whereSql")
                }
            }
        }

        return updateSql.toString()
    }
}