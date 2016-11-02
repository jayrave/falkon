package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

/**
 * All the table & column names passed here could contain SQL aliases & could also be qualified
 */
interface QuerySqlBuilder {

    /**
     * Builds a db specific statement similar to `SELECT ...`
     *
     * @param [tableName] the table to query from
     * @param [distinct] `true` if you want each row to be unique, `false` otherwise
     * @param [columns] A list of which columns (with potential aliases) to return, applied in
     * iteration order. Passing null will return all columns (akin to using `*` projection)
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
     */
    fun build(
            tableName: String,
            distinct: Boolean,
            columns: Iterable<SelectColumnInfo>?,
            joinInfos: Iterable<JoinInfo>?,
            whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?,
            limit: Long?,
            offset: Long?,
            argPlaceholder: String
    ): String
}