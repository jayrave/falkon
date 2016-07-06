package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class SimpleDeleteSqlBuilder : DeleteSqlBuilder {

    override fun build(
            tableName: String, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String): String {

        // Add basic delete stuff
        val deleteSql = StringBuilder(120)
        deleteSql.append("DELETE FROM $tableName")

        // Add where clause if required
        val whereSql = whereSections?.buildWhereClause(argPlaceholder)
        if (whereSql != null) {
            deleteSql.append(" $whereSql")
        }

        return deleteSql.toString()
    }
}