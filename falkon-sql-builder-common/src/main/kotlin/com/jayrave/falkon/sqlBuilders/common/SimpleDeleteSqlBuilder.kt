package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

object SimpleDeleteSqlBuilder {

    /**
     * Builds a `DELETE FROM ...` statement with the passed in info. Conditions for `WHERE`
     * clause are added in the iteration order of [whereSections]
     */
    fun build(tableName: String, whereSections: Iterable<WhereSection>?): String {

        // Add basic delete stuff
        val deleteSql = StringBuilder(120)
        deleteSql.append("DELETE FROM $tableName")

        // Add where clause if required
        val whereSql = whereSections?.buildWhereClause(ARG_PLACEHOLDER)
        if (whereSql != null) {
            deleteSql.append(" $whereSql")
        }

        return deleteSql.toString()
    }
}