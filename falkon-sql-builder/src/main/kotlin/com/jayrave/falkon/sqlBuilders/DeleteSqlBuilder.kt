package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface DeleteSqlBuilder {
    fun build(
            tableName: String,
            whereSections: Iterable<WhereSection>?,
            argPlaceholder: String
    ): String
}