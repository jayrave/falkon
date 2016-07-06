package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface QuerySqlBuilder {
    fun build(
            tableName: String,
            distinct: Boolean,
            columns: Iterable<String>?,
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