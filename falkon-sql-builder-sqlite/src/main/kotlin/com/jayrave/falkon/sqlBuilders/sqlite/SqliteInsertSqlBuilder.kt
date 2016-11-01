package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

class SqliteInsertSqlBuilder : InsertSqlBuilder {

    private val delegate = SimpleInsertSqlBuilder()

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String {

        return delegate.build(tableName, columns, argPlaceholder)
    }
}