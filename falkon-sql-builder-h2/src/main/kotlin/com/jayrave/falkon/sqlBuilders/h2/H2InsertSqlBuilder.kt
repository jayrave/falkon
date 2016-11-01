package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

class H2InsertSqlBuilder : InsertSqlBuilder {

    private val delegate = SimpleInsertSqlBuilder()

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String {

        return delegate.build(tableName, columns, argPlaceholder)
    }
}