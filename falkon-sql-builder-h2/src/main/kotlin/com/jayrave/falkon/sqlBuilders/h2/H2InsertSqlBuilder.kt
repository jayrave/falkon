package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

class H2InsertSqlBuilder : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = false

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = SimpleInsertSqlBuilder.build(tableName, columns, argPlaceholder)


    override fun buildInsertOrReplace(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String {

        throw UnsupportedOperationException(
                "As of Nov 1, 2016, H2 database doesn't support `insert or replace` functionality"
        )
    }
}