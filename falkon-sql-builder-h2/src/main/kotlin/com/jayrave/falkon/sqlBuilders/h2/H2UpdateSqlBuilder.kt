package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleUpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class H2UpdateSqlBuilder : UpdateSqlBuilder {

    override fun build(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String): String {

        return SimpleUpdateSqlBuilder.build(tableName, columns, whereSections, argPlaceholder)
    }
}