package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleDeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class H2DeleteSqlBuilder : DeleteSqlBuilder {

    override fun build(
            tableName: String, whereSections: Iterable<WhereSection>?,
            argPlaceholder: String): String {

        return SimpleDeleteSqlBuilder.build(tableName, whereSections, argPlaceholder)
    }
}