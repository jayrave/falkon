package com.jayrave.falkon.dao.insert.testLib

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder

class InsertSqlBuilderForTesting : InsertSqlBuilder {

    override val isInsertOrReplaceSupported: Boolean = false

    override fun build(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = "tableName: $tableName; columns: ${columns.joinToString()}"

    override fun buildInsertOrReplace(
            tableName: String, columns: Iterable<String>, argPlaceholder: String):
            String = throw UnsupportedOperationException("not implemented")
}