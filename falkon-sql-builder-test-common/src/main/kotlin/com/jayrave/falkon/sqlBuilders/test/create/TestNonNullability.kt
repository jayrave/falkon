package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.failIfOpDoesNotThrow
import com.jayrave.falkon.sqlBuilders.test.randomUuid
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestNonNullability(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `non-null column does not allow null values`() {

        // Create table with non-null column
        val columnInfo = ColumnInfoForTest("non_null_column", db.stringDataType, isNonNull = true)
        val tableInfo = TableInfoForTest("test", listOf(columnInfo), emptyList(), emptyList())
        db.execute(createTableSqlBuilder.build(tableInfo))

        // Insert SQL builder
        fun buildInsertSql(value: UUID?): String {
            return "INSERT INTO ${tableInfo.name} VALUES (${buildArgListForSql(value)})"
        }

        // Make sure records can be inserted
        db.execute(buildInsertSql(randomUuid()))
        assertThat(db.findRecordCountInTable(tableInfo.name)).isEqualTo(1)

        // Try inserting a record with null for a non-null column. It should throw
        failIfOpDoesNotThrow {
            db.execute(buildInsertSql(null))
        }
    }
}