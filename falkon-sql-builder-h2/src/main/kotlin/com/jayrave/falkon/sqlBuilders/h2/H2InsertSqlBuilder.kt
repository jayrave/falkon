package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

class H2InsertSqlBuilder : InsertSqlBuilder {

    override fun build(tableName: String, columns: Iterable<String>): String {
        return SimpleInsertSqlBuilder.build(tableName, columns)
    }
}