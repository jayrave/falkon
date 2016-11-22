package com.jayrave.falkon.dao.insertOrReplace.testLib

import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder

class InsertOrReplaceSqlBuilderForTesting : InsertOrReplaceSqlBuilder {

    override fun build(
            tableName: String, idColumns: Iterable<String>, nonIdColumns: Iterable<String>):
            String {

        return "tableName: $tableName; idColumns: ${idColumns.joinToString()}; " +
                "nonIdColumns: ${nonIdColumns.joinToString()}"
    }
}