package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.failIfOpDoesNotThrow
import com.jayrave.falkon.sqlBuilders.test.randomUuid
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestSingleColumnUniquenessConstraint(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `column with uniqueness constraint does not allow duplicates`() {

        // Create table with column with unique constraint
        val columnInfo = ColumnInfoForTest("unique_column", dataType = db.stringDataType)
        val tableInfo = TableInfoForTest(
                "test", listOf(columnInfo), listOf(listOf(columnInfo.name)), emptyList()
        )

        db.execute(createTableSqlBuilder.build(tableInfo))

        // Insert SQL builder
        fun buildInsertSql(idValue: UUID): String {
            return "INSERT INTO ${tableInfo.name} VALUES (${buildArgListForSql(idValue)})"
        }

        // Make sure records can be inserted
        val idToBeDuplicated = randomUuid()
        db.execute(buildInsertSql(idToBeDuplicated))
        assertThat(db.findRecordCountInTable(tableInfo.name)).isEqualTo(1)

        // Try inserting a record with duplicate value for a unique column. It should throw
        failIfOpDoesNotThrow {
            db.execute(buildInsertSql(idToBeDuplicated))
        }
    }
}