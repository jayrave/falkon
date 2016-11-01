package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleDeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class SqliteDeleteSqlBuilder : DeleteSqlBuilder {

    private val delegate = SimpleDeleteSqlBuilder()

    override fun build(
            tableName: String, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String): String {

        return delegate.build(tableName, whereSections, argPlaceholder)
    }
}