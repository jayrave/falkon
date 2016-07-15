package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface QuerySqlBuilder {

    /**
     * @param [tableName] the table to delete from
     * @param [distinct] `true` if you want each row to be unique, `false` otherwise
     * @param [columns] A list of which columns to return, applied in iteration order.
     * Passing null will return all columns
     * @param [joinInfos] A list of join information, applied in iterator order used to build
     * the optional SQL JOIN clause. Passing null denotes no JOIN in the build SQL
     * @param [whereSections] A list of sections, applied in iteration order used to build
     * the optional SQL WHERE clause. Passing null denotes no WHERE in the built SQL
     * @param [groupBy] A list of columns to SQL GROUP BY clause applied in iteration order.
     * Passing null will skip grouping
     * @param [orderBy] A list of [OrderInfo] to SQL ORDER BY clause applied in iteration order.
     * Passing null will skip ordering
     * @param [limit] Limits the number of rows returned by the query, formatted as LIMIT clause.
     * Passing null denotes no limit
     * @param [offset] Skips the requested number of rows from the beginning and then forms
     * the result set. Passing null denotes no offset
     * @param [argPlaceholder] to use as placeholders to prevent SQL injection
     * @param [orderByAscendingKey] text to use to denote ascending order in order by clause
     * @param [orderByDescendingKey] text to use to denote descending order in order by clause
     */
    fun build(
            tableName: String,
            distinct: Boolean,
            columns: Iterable<String>?,
            joinInfos: Iterable<JoinInfo>?,
            whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?,
            limit: Long?,
            offset: Long?,
            argPlaceholder: String,
            orderByAscendingKey: String,
            orderByDescendingKey: String
    ): String
}