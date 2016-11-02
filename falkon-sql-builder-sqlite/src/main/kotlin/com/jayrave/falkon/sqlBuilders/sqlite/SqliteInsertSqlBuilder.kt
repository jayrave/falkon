package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

/**
 * Take a look at https://www.sqlite.org/lang_conflict.html, to learn how `insert or replace`
 * works and make sure that it is what you want
 */
class SqliteInsertSqlBuilder : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = true
    private val delegate = SimpleInsertSqlBuilder({ "INSERT OR REPLACE" })

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = delegate.build(tableName, columns, argPlaceholder)


    override fun buildInsertOrReplace(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = delegate.buildInsertOrReplace(tableName, columns, argPlaceholder)
}