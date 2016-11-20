package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.SqlAndIndexToIndicesMap

/**
 * Take a look at https://www.sqlite.org/lang_conflict.html, to learn how `insert or replace`
 * works and make sure that it is what you want
 */
class SqliteInsertSqlBuilder : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = true

    override fun build(tableName: String, columns: Iterable<String>): String {
        return SimpleInsertSqlBuilder.build(tableName, columns)
    }

    override fun buildInsertOrReplace(
            tableName: String, idColumns: Iterable<String>, nonIdColumns: Iterable<String>):
            SqlAndIndexToIndicesMap {

        return SimpleInsertSqlBuilder.buildInsertOrReplace(
                "INSERT OR REPLACE", tableName, idColumns, nonIdColumns
        )
    }
}