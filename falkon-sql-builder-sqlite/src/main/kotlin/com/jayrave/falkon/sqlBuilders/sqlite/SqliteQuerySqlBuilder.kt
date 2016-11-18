package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleQuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import java.sql.SQLSyntaxErrorException

class SqliteQuerySqlBuilder : QuerySqlBuilder {

    override fun build(
            tableName: String, distinct: Boolean, columns: Iterable<SelectColumnInfo>?,
            joinInfos: Iterable<JoinInfo>?, whereSections: Iterable<WhereSection>?,
            groupBy: Iterable<String>?, orderBy: Iterable<OrderInfo>?, limit: Long?,
            offset: Long?): String {

        // Throw for unsupported feature
        if (joinInfos?.any { it.type == JoinInfo.Type.RIGHT_OUTER_JOIN } ?: false) {
            throw SQLSyntaxErrorException(
                    "Sqlite doesn't support RIGHT OUTER JOIN. Take a look @ " +
                            "https://www.sqlite.org/omitted.html for further explanation"
            )
        }

        // https://www.sqlite.org/lang_select.html
        // Sqlite `OFFSET` is a part of its `LIMIT` expression. So, if an offset is
        // asked for without giving a limit, use a negative value for limit which
        // denotes having no upper bound
        val sqliteLimit = when {
            limit != null -> limit
            else -> when {
                offset != null -> -1L
                else -> null
            }
        }

        return SimpleQuerySqlBuilder.build(
                tableName, distinct, columns, joinInfos, whereSections, groupBy,
                orderBy, sqliteLimit, offset
        )
    }
}