package com.jayrave.falkon.sqlBuilders.test.insert

import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.execute
import com.jayrave.falkon.sqlBuilders.test.findAllRecordsInTable
import org.assertj.core.api.Assertions.assertThat
import java.sql.Types

class TestInsertOrReplace(private val insertSqlBuilder: InsertSqlBuilder, db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        // Create table
        dataSource.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$INT_COLUMN_NAME ${db.intDataType} PRIMARY KEY, " +
                        "$STRING_COLUMN_NAME ${db.stringDataType})"
        )
    }


    fun `new records are inserted via insert or replace with both null & non null values`() {

        // Build sql & index map
        val (insertOrReplaceSql, indexToIndicesMap) = insertSqlBuilder.buildInsertOrReplace(
                TABLE_NAME, ID_COLUMNS, NON_ID_COLUMNS
        )

        // Insert records
        dataSource.execute(insertOrReplaceSql) { ps ->
            indexToIndicesMap.indicesForIndex(1).forEach { ps.setInt(it, 5) }
            indexToIndicesMap.indicesForIndex(2).forEach { ps.setString(it, "test 6") }
        }

        dataSource.execute(insertOrReplaceSql) { ps ->
            ps.clearParameters()
            indexToIndicesMap.indicesForIndex(1).forEach { ps.setInt(it, 7) }
            indexToIndicesMap.indicesForIndex(2).forEach { ps.setNull(it, Types.VARCHAR) }
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
        dataSource.execute("INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(5, "test 6")})")
        dataSource.execute("INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(7, null)})")

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

        // Build sql & index map
        val (insertOrReplaceSql, indexToIndicesMap) = insertSqlBuilder.buildInsertOrReplace(
                TABLE_NAME, ID_COLUMNS, NON_ID_COLUMNS
        )

        // Update records via insert or replace
        dataSource.execute(insertOrReplaceSql) { ps ->
            indexToIndicesMap.indicesForIndex(1).forEach { ps.setInt(it, 5) }
            indexToIndicesMap.indicesForIndex(2).forEach { ps.setNull(it, Types.VARCHAR) }
        }

        dataSource.execute(insertOrReplaceSql) { ps ->
            ps.clearParameters()
            indexToIndicesMap.indicesForIndex(1).forEach { ps.setInt(it, 7) }
            indexToIndicesMap.indicesForIndex(2).forEach { ps.setString(it, "test 8") }
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