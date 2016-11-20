package com.jayrave.falkon.dao.insert.testLib

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.SqlAndIndexToIndicesMap

class InsertSqlBuilderForTesting : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = false

    override fun build(tableName: String, columns: Iterable<String>): String {
        return "tableName: $tableName; columns: ${columns.joinToString()}"
    }

    override fun buildInsertOrReplace(
            tableName: String, idColumns: Iterable<String>,
            nonIdColumns: Iterable<String>): SqlAndIndexToIndicesMap {

        throw UnsupportedOperationException("not implemented")
    }
}