package com.jayrave.falkon.sqlBuilders.test.insert

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.execute
import com.jayrave.falkon.sqlBuilders.test.findAllRecordsInTable
import org.assertj.core.api.Assertions.assertThat
import java.sql.Types

class TestInsert(
        private val insertSqlBuilder: InsertSqlBuilder,
        private val db: DbForTest) {

    fun `can insert both null & non null values`() {
        val dataSource = db.dataSource

        // Create table
        dataSource.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$INT_COLUMN_NAME ${db.intDataType}, " +
                        "$STRING_COLUMN_NAME ${db.stringDataType})"
        )

        // Build insert sql
        val insertSql = insertSqlBuilder.build(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME)
        )

        // Insert records
        dataSource.execute(insertSql) { ps ->
            ps.setInt(1, 5)
            ps.setString(2, "test 6")
        }

        dataSource.execute(insertSql) { ps ->
            ps.clearParameters()
            ps.setInt(1, 7)
            ps.setNull(2, Types.VARCHAR)
        }

        dataSource.execute(insertSql) { ps ->
            ps.clearParameters()
            ps.setNull(1, Types.INTEGER)
            ps.setString(2, "test 8")
        }

        // Query for all records
        val allRecords = dataSource.findAllRecordsInTable(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecords).hasSize(3)
        assertThat(allRecords[0]).containsEntry(INT_COLUMN_NAME, 5.toString())
        assertThat(allRecords[0]).containsEntry(STRING_COLUMN_NAME, "test 6")
        assertThat(allRecords[1]).containsEntry(INT_COLUMN_NAME, 7.toString())
        assertThat(allRecords[1]).containsEntry(STRING_COLUMN_NAME, null)
        assertThat(allRecords[2]).containsEntry(INT_COLUMN_NAME, null)
        assertThat(allRecords[2]).containsEntry(STRING_COLUMN_NAME, "test 8")
    }


    companion object {
        private const val TABLE_NAME = "test"
        private const val INT_COLUMN_NAME = "int"
        private const val STRING_COLUMN_NAME = "string"
    }
}