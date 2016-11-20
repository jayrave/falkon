package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleInsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.SqlAndIndexToIndicesMap

class H2InsertSqlBuilder : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = false

    override fun build(tableName: String, columns: Iterable<String>): String {
        return SimpleInsertSqlBuilder.build(tableName, columns)
    }

    override fun buildInsertOrReplace(
            tableName: String, idColumns: Iterable<String>,
            nonIdColumns: Iterable<String>): SqlAndIndexToIndicesMap {

        return SimpleInsertSqlBuilder.buildInsertOrReplace(
                "MERGE", tableName, idColumns, nonIdColumns
        )
    }
}