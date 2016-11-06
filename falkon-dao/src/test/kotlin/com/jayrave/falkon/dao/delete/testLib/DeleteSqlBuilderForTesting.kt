package com.jayrave.falkon.dao.delete.testLib

import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class DeleteSqlBuilderForTesting : DeleteSqlBuilder {
    override fun build(tableName: String, whereSections: Iterable<WhereSection>?): String {
        val whereClause = buildWhereClauseWithPlaceholders(whereSections)
        return "tableName: $tableName; whereClause: $whereClause"
    }
}