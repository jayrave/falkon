package com.jayrave.falkon.dao.query.testLib

import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class QuerySqlBuilderForTesting : QuerySqlBuilder {

    override fun build(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            joinInfos: Iterable<JoinInfo>?, whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?, orderBy: Iterable<OrderInfo>?, limit: Long?,
            offset: Long?, argPlaceholder: String, orderByAscendingKey: String,
            orderByDescendingKey: String): String {

        val columnsString = columns?.joinToString()
        val whereString = buildWhereClauseWithPlaceholders(whereSections)
        val groupByString = groupBy?.joinToString()
        val orderByString = orderBy?.joinToString() { "${it.columnName} ${it.ascending}" }
        val joinString = joinInfos?.joinToString {
            "${it.nameOfTableToJoin} ${it.qualifiedLocalColumnName} " +
                    "${it.nameOfTableToJoin} ${it.qualifiedColumnNameFromTableToJoin}"
        }

        return "tableName: $tableName; " +
                "distinct: $distinct; " +
                "columns: $columnsString; " +
                "join: $joinString; " +
                "where: $whereString; " +
                "groupBy: $groupByString; " +
                "orderBy: $orderByString; " +
                "limit: $limit; " +
                "offset: $offset"
    }
}