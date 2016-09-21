package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class SimpleQuerySqlBuilder : QuerySqlBuilder {

    override fun build(
            tableName: String, distinct: Boolean, columns: Iterable<SelectColumnInfo>?,
            joinInfos: Iterable<JoinInfo>?, whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?, orderBy: Iterable<OrderInfo>?, limit: Long?,
            offset: Long?, argPlaceholder: String): String {

        val querySql = StringBuilder(120)
        querySql.append("SELECT")

        querySql.addDistinctIfRequired(distinct)
        querySql.addColumnsToBeSelected(columns)
        querySql.append(" FROM")

        // JOIN clause takes care of including the table name. If there is no JOIN clause,
        // the table name has to be included exclusively
        if (!querySql.addJoinsIfPossible(joinInfos, tableName)) {
            querySql.append(" $tableName")
        }

        querySql.addWhereIfPossible(whereSections, argPlaceholder)
        querySql.addGroupIfPossible(groupBy)
        querySql.addOrderByIfPossible(orderBy)
        querySql.addLimitIfPossible(limit)
        querySql.addOffsetIfPossible(offset)

        return querySql.toString()
    }


    private fun StringBuilder.addDistinctIfRequired(distinct: Boolean) {
        if (distinct) {
            append(" DISTINCT")
        }
    }


    private fun StringBuilder.addColumnsToBeSelected(columns: Iterable<SelectColumnInfo>?) {
        append(' ') // Add separator
        val columnSelection = columns.joinToStringIfHasItems {
            val alias = it.alias
            when (alias) {
                null -> it.columnName
                else -> "${it.columnName} AS $alias"
            }
        }

        when (isValidPart(columnSelection)) {
            true -> append(columnSelection) // Add comma separated column names
            else -> append("*") // No columns were exclusively requested. Get back all
        }
    }


    /**
     * @return `true` if JOIN clause was added; `false` otherwise
     */
    private fun StringBuilder.addJoinsIfPossible(
            joinInfos: Iterable<JoinInfo>?, firstTableNameForFirstJoin: String)
            : Boolean {

        val joinSql = joinInfos?.buildJoinClause(firstTableNameForFirstJoin)
        return when {
            !isValidPart(joinSql) -> false
            else -> {
                append(" $joinSql")
                true
            }
        }
    }


    private fun StringBuilder.addWhereIfPossible(
            whereSections: Iterable<WhereSection>?, argPlaceholder: String) {

        val whereSql = whereSections?.buildWhereClause(argPlaceholder)
        if (isValidPart(whereSql)) {
            append(" $whereSql")
        }
    }


    private fun StringBuilder.addGroupIfPossible(groupBy: Iterable<String>?) {
        val groupBySql = groupBy.joinToStringIfHasItems(prefix = " GROUP BY ") { it }
        if (isValidPart(groupBySql)) {
            append(groupBySql)
        }
    }


    private fun StringBuilder.addOrderByIfPossible(orderBy: Iterable<OrderInfo>?) {
        val orderBySql = orderBy.joinToStringIfHasItems(prefix = " ORDER BY ") {
            val order = if (it.ascending) "ASC" else "DESC"
            "${it.columnName} $order"
        }

        if (isValidPart(orderBySql)) {
            append(orderBySql)
        }
    }


    private fun StringBuilder.addLimitIfPossible(limit: Long?) {
        if (limit != null) {
            append(" LIMIT $limit")
        }
    }


    private fun StringBuilder.addOffsetIfPossible(offset: Long?) {
        if (offset != null) {
            append(" OFFSET $offset")
        }
    }


    private fun isValidPart(sqlString: String?): Boolean {
        return !sqlString.isNullOrBlank()
    }
}