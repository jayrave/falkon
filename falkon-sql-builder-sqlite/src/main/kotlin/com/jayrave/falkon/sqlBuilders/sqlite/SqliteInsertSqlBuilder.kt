package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

/**
 * Take a look at https://www.sqlite.org/lang_conflict.html, to learn how `insert or replace`
 * works and make sure that it is what you want
 */
class SqliteInsertSqlBuilder : InsertSqlBuilder {

    private val delegate = SimpleInsertSqlBuilder({ "INSERT OR REPLACE" })

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String {

        return delegate.build(tableName, columns, argPlaceholder)
    }
}