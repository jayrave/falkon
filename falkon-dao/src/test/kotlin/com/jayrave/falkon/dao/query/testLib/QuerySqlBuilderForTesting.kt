package com.jayrave.falkon.dao.query.testLib

import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class QuerySqlBuilderForTesting : QuerySqlBuilder {

    override fun build(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?, argPlaceholder: String,
            orderByAscendingKey: String, orderByDescendingKey: String): String {

        val whereClause = buildWhereClauseWithPlaceholders(whereSections)
        val orderByString = orderBy?.joinToString() { "${it.columnName} ${it.ascending}" }
        return "tableName: $tableName; distinct: $distinct; " +
                "columns: ${columns?.joinToString()}; whereClause: $whereClause; " +
                "groupBy: ${groupBy?.joinToString()}; orderBy: $orderByString; " +
                "limit: $limit; offset: $offset"
    }
}