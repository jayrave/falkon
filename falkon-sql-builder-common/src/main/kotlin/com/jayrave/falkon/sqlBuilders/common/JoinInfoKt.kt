package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.JoinInfo

/**
 * A SQL JOIN is built from the passed in [JoinInfo]s
 *
 * @return - `null` if the iterable is empty or a WHERE clause (along with WHERE keyword)
 */
internal fun Iterable<JoinInfo>.buildJoinClause(firstTableNameForFirstJoin: String): String? {
    val clause = StringBuilder()
    this.forEach { joinInfo ->
        // First table name for first JOIN clause will be added later. For subsequent
        // JOIN clauses, the previous JOINS would act as the first table

        clause
                .append(SPACE)
                .append(joinInfo.type.sqlText())
                .append(SPACE)
                .append(joinInfo.nameOfTableToJoin)
                .append(" ON ")
                .append(joinInfo.qualifiedLocalColumnName)
                .append(" = ")
                .append(joinInfo.qualifiedColumnNameFromTableToJoin)
    }

    return when {
        clause.isEmpty() -> null
        else -> {
            clause.insert(0, firstTableNameForFirstJoin)
            clause.toString()
        }
    }
}


private const val SPACE = ' '
private fun JoinInfo.Type.sqlText(): String {
    return when (this) {
        JoinInfo.Type.INNER_JOIN -> "INNER JOIN"
        JoinInfo.Type.LEFT_OUTER_JOIN -> "LEFT OUTER JOIN"
        JoinInfo.Type.RIGHT_OUTER_JOIN -> "RIGHT OUTER JOIN"
    }
}