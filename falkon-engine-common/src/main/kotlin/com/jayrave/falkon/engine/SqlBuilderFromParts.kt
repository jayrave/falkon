package com.jayrave.falkon.engine

import java.sql.SQLSyntaxErrorException

/**
 * A convenience object to build SQL commands from the passed in parts. This object is mainly
 * for use by [Engine] implementations
 */
object SqlBuilderFromParts {

    fun buildInsertSqlOrNull(
            tableName: String, columns: Iterable<String>,
            argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String? {

        return buildInsertSqlFromParts(tableName, columns, argPlaceholder)
    }


    /**
     * @throws SQLSyntaxErrorException if the passed in [columns] is empty
     */
    fun buildInsertSqlOrThrow(
            tableName: String, columns: Iterable<String>,
            argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String {

        val insertSql = buildInsertSqlOrNull(tableName, columns, argPlaceholder)
        when {
            insertSql != null -> return insertSql
            else -> throw SQLSyntaxErrorException("Trying to build INSERT without any columns")
        }
    }


    fun buildUpdateSqlOrNull(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String? {

        return buildUpdateSqlFromParts(tableName, columns, whereSections, argPlaceholder)
    }


    /**
     * @throws SQLSyntaxErrorException if the passed in [columns] is empty
     */
    fun buildUpdateSqlOrThrow(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String {

        val updateSql = buildUpdateSqlOrNull(tableName, columns, whereSections, argPlaceholder)
        when {
            updateSql != null -> return updateSql
            else -> throw SQLSyntaxErrorException("Trying to build UPDATE without any columns")
        }
    }


    fun buildDeleteSql(
            tableName: String, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER): String {
        return buildDeleteSqlFromParts(tableName, whereSections, argPlaceholder)
    }


    fun buildQuerySql(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?,
            argPlaceholder: String = DEFAULT_ARG_PLACEHOLDER,
            orderByAscendingKey: String = ORDER_BY_ASC_KEY,
            orderByDescendingKey: String = ORDER_BY_DESC_KEY): String {

        return buildQuerySqlFromParts(
                tableName, distinct, columns, whereSections, groupBy, orderBy, limit, offset,
                argPlaceholder, orderByAscendingKey, orderByDescendingKey
        )
    }
}