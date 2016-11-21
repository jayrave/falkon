package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertOrReplaceSqlBuilder

/**
 * Take a look at http://www.h2database.com/html/grammar.html#merge, to learn how
 * `insert or replace` works and make sure that it is what you want
 */
class H2InsertOrReplaceSqlBuilder : InsertOrReplaceSqlBuilder {

    override fun build(
            tableName: String, idColumns: Iterable<String>, nonIdColumns: Iterable<String>):
            String {

        return SimpleInsertOrReplaceSqlBuilder.build("MERGE", tableName, idColumns, nonIdColumns)
    }
}