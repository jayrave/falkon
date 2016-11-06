package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleUpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection

class SqliteUpdateSqlBuilder : UpdateSqlBuilder {

    override fun build(
            tableName: String, columns: Iterable<String>, whereSections: Iterable<WhereSection>?):
            String {

        return SimpleUpdateSqlBuilder.build(tableName, columns, whereSections)
    }
}