package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

class SqliteInsertSqlBuilder : InsertSqlBuilder {

    override fun build(tableName: String, columns: Iterable<String>): String {
        return SimpleInsertSqlBuilder.build(tableName, columns)
    }
}