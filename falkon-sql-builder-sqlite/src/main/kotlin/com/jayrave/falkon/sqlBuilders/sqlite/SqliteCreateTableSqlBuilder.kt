package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleCreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.TableInfo

class SqliteCreateTableSqlBuilder : CreateTableSqlBuilder {
    override fun build(tableInfo: TableInfo): List<String> = listOf(
            SimpleCreateTableSqlBuilder.build(tableInfo, "AUTOINCREMENT")
    )
}