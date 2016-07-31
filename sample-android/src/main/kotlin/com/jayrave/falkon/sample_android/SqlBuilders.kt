package com.jayrave.falkon.sample_android

import com.jayrave.falkon.sqlBuilders.*

data class SqlBuilders(
        val createTableSqlBuilder: CreateTableSqlBuilder,
        val insertSqlBuilder: InsertSqlBuilder,
        val updateSqlBuilder: UpdateSqlBuilder,
        val deleteSqlBuilder: DeleteSqlBuilder,
        val querySqlBuilder: QuerySqlBuilder
)