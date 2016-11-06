package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleQuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class SqliteQuerySqlBuilder : QuerySqlBuilder {

    override fun build(
            tableName: String, distinct: Boolean, columns: Iterable<SelectColumnInfo>?,
            joinInfos: Iterable<JoinInfo>?, whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?, orderBy: Iterable<OrderInfo>?, limit: Long?,
            offset: Long?): String {

        return SimpleQuerySqlBuilder.build(
                tableName, distinct, columns, joinInfos, whereSections, groupBy,
                orderBy, limit, offset
        )
    }
}