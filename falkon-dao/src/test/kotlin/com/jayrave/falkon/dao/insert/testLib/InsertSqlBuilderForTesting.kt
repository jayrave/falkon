package com.jayrave.falkon.dao.insert.testLib

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder

class InsertSqlBuilderForTesting : InsertSqlBuilder {

    override fun build(tableName: String, columns: Iterable<String>): String {
        return "tableName: $tableName; columns: ${columns.joinToString()}"
    }
}