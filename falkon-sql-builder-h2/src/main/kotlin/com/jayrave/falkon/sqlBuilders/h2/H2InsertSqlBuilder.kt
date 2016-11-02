package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder

class H2InsertSqlBuilder : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = false
    private val delegate = SimpleInsertSqlBuilder({ throwForInsertOrReplace() })

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = delegate.build(tableName, columns, argPlaceholder)


    override fun buildInsertOrReplace(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = throwForInsertOrReplace()


    private fun <R> throwForInsertOrReplace(): R {
        throw UnsupportedOperationException(
                "As of Nov 1, 2016, H2 database doesn't support `insert or replace` functionality"
        )
    }
}