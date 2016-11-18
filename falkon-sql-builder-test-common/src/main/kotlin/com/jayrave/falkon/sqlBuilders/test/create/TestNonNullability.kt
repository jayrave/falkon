package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestNonNullability(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `non-null column does not allow null values`() {
        val dataSource = db.dataSource

        // Create table with non-null column
        val columnInfo = ColumnInfoForTest("non_null_column", db.stringDataType, isNonNull = true)
        val tableInfo = TableInfoForTest("test", listOf(columnInfo), emptyList(), emptyList())
        dataSource.execute(createTableSqlBuilder.build(tableInfo))

        // Insert SQL builder
        fun buildInsertSql(value: UUID?): String {
            return "INSERT INTO ${tableInfo.name} VALUES (${buildArgListForSql(value)})"
        }

        // Make sure records can be inserted
        dataSource.execute(buildInsertSql(randomUuid()))
        assertThat(dataSource.findRecordCountInTable(tableInfo.name)).isEqualTo(1)

        // Try inserting a record with null for a non-null column. It should throw
        failIfOpDoesNotThrow {
            dataSource.execute(buildInsertSql(null))
        }
    }
}