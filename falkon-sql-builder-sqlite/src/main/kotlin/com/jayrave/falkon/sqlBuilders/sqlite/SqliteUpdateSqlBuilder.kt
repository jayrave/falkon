package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleUpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class SqliteUpdateSqlBuilder : UpdateSqlBuilder {

    private val delegate = SimpleUpdateSqlBuilder()

    override fun build(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String): String {

        return delegate.build(tableName, columns, whereSections, argPlaceholder)
    }
}