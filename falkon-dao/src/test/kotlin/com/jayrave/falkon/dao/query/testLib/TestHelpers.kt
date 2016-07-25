package com.jayrave.falkon.dao.query.testLib

import com.jayrave.falkon.dao.query.Query
import com.jayrave.falkon.dao.testLib.EngineForTestingBuilders
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import org.assertj.core.api.Assertions

const val ARG_PLACEHOLDER = "?"


internal fun assertQueryEquality(actualQuery: Query, expectedQuery: Query) {
    Assertions.assertThat(actualQuery.sql).isEqualTo(expectedQuery.sql)
    Assertions.assertThat(actualQuery.arguments).containsExactlyElementsOf(expectedQuery.arguments)
}


internal fun buildQuerySql(
        tableName: String, querySqlBuilder: QuerySqlBuilder, distinct: Boolean = false,
        columns: Iterable<String>? = null, joinInfos: Iterable<JoinInfo>? = null,
        whereSections: Iterable<WhereSection>? = null, groupBy: Iterable<String>? = null,
        orderBy: Iterable<OrderInfo>? = null, limit: Long? = null, offset: Long? = null):
        String {

    return querySqlBuilder.build(
            tableName = tableName, distinct = distinct, columns = columns,
            joinInfos = joinInfos, whereSections = whereSections, groupBy = groupBy,
            orderBy = orderBy, limit = limit, offset = offset,
            argPlaceholder = ARG_PLACEHOLDER
    )
}



internal data class JoinInfoForTest(
        override val type: JoinInfo.Type,
        override val qualifiedLocalColumnName: String,
        override val nameOfTableToJoin: String,
        override val qualifiedColumnNameFromTableToJoin: String) :
        JoinInfo



internal data class OrderInfoForTest(
        override val columnName: String, override val ascending: Boolean) :
        OrderInfo



internal class Bundle(
        val table: TableForTest, val engine: EngineForTestingBuilders,
        val querySqlBuilder: QuerySqlBuilder) {

    companion object {
        fun default(): Bundle {
            val engine = EngineForTestingBuilders.createWithOneShotStatements()
            val table = TableForTest(configuration = defaultTableConfiguration(engine))
            val querySqlBuilder = QuerySqlBuilderForTesting()
            return Bundle(table, engine, querySqlBuilder)
        }
    }
}
