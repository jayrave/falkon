package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleCreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.TableInfo

class H2CreateTableSqlBuilder : CreateTableSqlBuilder {

    private val delegate = SimpleCreateTableSqlBuilder("AUTO_INCREMENT")

    override fun build(tableInfo: TableInfo): List<String> {
        return listOf(delegate.build(tableInfo))
    }
}