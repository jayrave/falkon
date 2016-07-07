package com.jayrave.falkon.dao.update.testLib

import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class UpdateSqlBuilderForTesting : UpdateSqlBuilder {

    override fun build(
            tableName: String, columns: Iterable<String>,
            whereSections: Iterable<WhereSection>?, argPlaceholder: String): String {
        val whereClause = buildWhereClauseWithPlaceholders(whereSections)
        return "tableName: $tableName; columns: ${columns.joinToString()}; " +
                "whereClause: $whereClause"
    }
}