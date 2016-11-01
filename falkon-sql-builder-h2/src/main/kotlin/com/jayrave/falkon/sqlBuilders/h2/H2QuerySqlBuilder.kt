package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleQuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class H2QuerySqlBuilder : QuerySqlBuilder {

    private val delegate = SimpleQuerySqlBuilder()

    override fun build(
            tableName: String, distinct: Boolean, columns: Iterable<SelectColumnInfo>?,
            joinInfos: Iterable<JoinInfo>?, whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?, orderBy: Iterable<OrderInfo>?, limit: Long?,
            offset: Long?, argPlaceholder: String): String {

        return delegate.build(
                tableName, distinct, columns, joinInfos, whereSections, groupBy,
                orderBy, limit, offset, argPlaceholder
        )
    }
}