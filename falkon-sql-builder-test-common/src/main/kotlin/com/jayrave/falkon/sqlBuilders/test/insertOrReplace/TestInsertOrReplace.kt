package com.jayrave.falkon.sqlBuilders.test.insertOrReplace

import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.execute
import com.jayrave.falkon.sqlBuilders.test.findAllRecordsInTable
import org.assertj.core.api.Assertions.assertThat
import java.sql.Types

class TestInsertOrReplace(
        private val insertOrReplaceSqlBuilder: InsertOrReplaceSqlBuilder, db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        // Create table
        dataSource.execute(
                "CREATE TABLE ${TABLE_NAME} (" +
                        "${INT_COLUMN_NAME} ${db.intDataType} PRIMARY KEY, " +
                        "${STRING_COLUMN_NAME} ${db.stringDataType})"
        )
    }


    fun `new records are inserted via insert or replace with both null & non null values`() {
        // Build sql
        val sql = insertOrReplaceSqlBuilder.build(TABLE_NAME, ID_COLUMNS, NON_ID_COLUMNS)

        // Insert records
        dataSource.execute(sql) { ps ->
            ps.setInt(1, 5)
            ps.setString(2, "test 6")
        }

        dataSource.execute(sql) { ps ->
            ps.clearParameters()
            ps.setInt(1, 7)
            ps.setNull(2, Types.VARCHAR)
        }

        // Query for all records
        val allRecords = dataSource.findAllRecordsInTable(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecords).hasSize(2)
        assertThat(allRecords[0]).containsEntry(INT_COLUMN_NAME, 5.toString())
        assertThat(allRecords[0]).containsEntry(STRING_COLUMN_NAME, "test 6")
        assertThat(allRecords[1]).containsEntry(INT_COLUMN_NAME, 7.toString())
        assertThat(allRecords[1]).containsEntry(STRING_COLUMN_NAME, null)
    }


    fun `existing records are updated via insert or replace with both null & non null values`() {
        // Insert records
        dataSource.execute("INSERT INTO ${TABLE_NAME} VALUES(${buildArgListForSql(5, "test 6")})")
        dataSource.execute("INSERT INTO ${TABLE_NAME} VALUES(${buildArgListForSql(7, null)})")

        // Query for all records
        val allRecords = dataSource.findAllRecordsInTable(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecords).hasSize(2)
        assertThat(allRecords[0]).containsEntry(INT_COLUMN_NAME, 5.toString())
        assertThat(allRecords[0]).containsEntry(STRING_COLUMN_NAME, "test 6")
        assertThat(allRecords[1]).containsEntry(INT_COLUMN_NAME, 7.toString())
        assertThat(allRecords[1]).containsEntry(STRING_COLUMN_NAME, null)

        // Build sql
        val sql = insertOrReplaceSqlBuilder.build(TABLE_NAME, ID_COLUMNS, NON_ID_COLUMNS)

        // Update records via insert or replace
        dataSource.execute(sql) { ps ->
            ps.setInt(1, 5)
            ps.setNull(2, Types.VARCHAR)
        }

        dataSource.execute(sql) { ps ->
            ps.clearParameters()
            ps.setInt(1, 7)
            ps.setString(2, "test 8")
        }

        // Query for all records
        val allRecordsAfterUpdate = dataSource.findAllRecordsInTable(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecordsAfterUpdate).hasSize(2)
        assertThat(allRecordsAfterUpdate[0]).containsEntry(INT_COLUMN_NAME, 5.toString())
        assertThat(allRecordsAfterUpdate[0]).containsEntry(STRING_COLUMN_NAME, null)
        assertThat(allRecordsAfterUpdate[1]).containsEntry(INT_COLUMN_NAME, 7.toString())
        assertThat(allRecordsAfterUpdate[1]).containsEntry(STRING_COLUMN_NAME, "test 8")
    }



    companion object {
        private const val TABLE_NAME = "test"
        private const val INT_COLUMN_NAME = "int"
        private const val STRING_COLUMN_NAME = "string"

        private val ID_COLUMNS = listOf(INT_COLUMN_NAME)
        private val NON_ID_COLUMNS = listOf(STRING_COLUMN_NAME)
    }
}