package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.failIfOpDoesNotThrow
import com.jayrave.falkon.sqlBuilders.test.randomUuid
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestMultiColumnUniquenessConstraint(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `multi column uniqueness constraint does not allow duplicates`() {

        // Create table with column with unique constraint
        val column1Info = ColumnInfoForTest("column_1", dataType = db.stringDataType)
        val column2Info = ColumnInfoForTest("column_2", dataType = db.stringDataType)
        val tableInfo = TableInfoForTest(
                "test", listOf(column1Info, column2Info),
                listOf(listOf(column1Info.name, column2Info.name)), emptyList()
        )

        db.execute(createTableSqlBuilder.build(tableInfo))

        // Insert SQL builder
        fun buildInsertSql(value1: UUID, value2: UUID): String {
            return "INSERT INTO ${tableInfo.name} VALUES (${buildArgListForSql(value1, value2)})"
        }

        // Make sure records can be inserted
        val value1ToBeDuplicated = randomUuid()
        val value2ToBeDuplicated = randomUuid()
        db.execute(buildInsertSql(value1ToBeDuplicated, value2ToBeDuplicated))
        db.execute(buildInsertSql(value1ToBeDuplicated, randomUuid()))
        db.execute(buildInsertSql(randomUuid(), value2ToBeDuplicated))
        assertThat(db.findRecordCountInTable(tableInfo.name)).isEqualTo(3)

        // Try inserting a record with duplicate value. It should throw
        failIfOpDoesNotThrow {
            db.execute(buildInsertSql(value1ToBeDuplicated, value2ToBeDuplicated))
        }
    }
}