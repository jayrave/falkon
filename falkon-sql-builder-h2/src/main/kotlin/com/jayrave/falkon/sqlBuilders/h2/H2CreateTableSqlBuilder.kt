package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleCreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.TableInfo

class H2CreateTableSqlBuilder : CreateTableSqlBuilder {
    override fun build(tableInfo: TableInfo): List<String> = listOf(
            SimpleCreateTableSqlBuilder.build(tableInfo, "AUTO_INCREMENT")
    )
}