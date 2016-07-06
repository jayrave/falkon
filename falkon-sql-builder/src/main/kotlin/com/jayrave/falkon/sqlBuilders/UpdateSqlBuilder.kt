package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface UpdateSqlBuilder {
    fun build(
            tableName: String,
            columns: Iterable<String>,
            whereSections: Iterable<WhereSection>?,
            argPlaceholder: String
    ): String
}