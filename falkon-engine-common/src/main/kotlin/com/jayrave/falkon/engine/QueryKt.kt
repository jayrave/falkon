package com.jayrave.falkon.engine

/**
 * @return a SQL statement built from the parts
 */
internal fun buildQuerySqlFromParts(
        tableName: String, distinct: Boolean, columns: Iterable<String>?,
        whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
        orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?, argPlaceholder: String,
        orderByAscendingKey: String, orderByDescendingKey: String): String {

    val querySql = StringBuilder(120)
    querySql.append("SELECT")

    querySql.addDistinctIfRequired(distinct)
    querySql.addColumnsToBeSelected(columns)
    querySql.append(" FROM $tableName")
    querySql.addWhereIfPossible(whereSections, argPlaceholder)
    querySql.addGroupIfPossible(groupBy)
    querySql.addOrderByIfPossible(orderBy, orderByAscendingKey, orderByDescendingKey)
    querySql.addLimitIfPossible(limit)
    querySql.addOffsetIfPossible(offset)

    return querySql.toString()
}


private fun StringBuilder.addDistinctIfRequired(distinct: Boolean) {
    if (distinct) {
        append(" DISTINCT")
    }
}


private fun StringBuilder.addColumnsToBeSelected(columns: Iterable<String>?) {
    append(' ') // Add separator
    val columnSelection = columns.joinToStringIfHasItems { it }
    when (isValidPart(columnSelection)) {
        true -> append(columnSelection) // Add comma separated column names
        else -> append("*") // No columns were exclusively requested. Get back all
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


private fun StringBuilder.addOrderByIfPossible(
        orderBy: Iterable<OrderInfo>?, orderByAscendingFlag: String,
        orderByDescendingFlag: String) {

    val orderBySql = orderBy.joinToStringIfHasItems(prefix = " ORDER BY ") {
        val flag = if (it.ascending) orderByAscendingFlag else orderByDescendingFlag
        "${it.columnName} $flag"
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


private fun <T> Iterable<T>?.joinToStringIfHasItems(
        separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "",
        transform: ((T) -> CharSequence)): String? {

    return when (this) {
        null -> null
        else -> {
            var count = 0
            val string = joinToString(separator = separator, prefix = prefix, postfix = postfix) {
                count++
                transform.invoke(it)
            }

            when (count) {
                0 -> null
                else -> string
            }
        }
    }
}


private fun isValidPart(sqlString: String?): Boolean {
    return !sqlString.isNullOrBlank()
}
