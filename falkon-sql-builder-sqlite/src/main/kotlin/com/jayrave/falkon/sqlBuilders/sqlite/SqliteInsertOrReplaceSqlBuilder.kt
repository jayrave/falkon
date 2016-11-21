package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertOrReplaceSqlBuilder

/**
 * Take a look at https://www.sqlite.org/lang_conflict.html, to learn how `insert or replace`
 * works and make sure that it is what you want
 */
class SqliteInsertOrReplaceSqlBuilder : InsertOrReplaceSqlBuilder {

    override fun build(
            tableName: String, idColumns: Iterable<String>, nonIdColumns: Iterable<String>):
            String {

        return SimpleInsertOrReplaceSqlBuilder.build(
                "INSERT OR REPLACE", tableName, idColumns, nonIdColumns
        )
    }
}