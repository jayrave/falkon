package com.jayrave.falkon.sqlBuilders.test.insertOrReplace

import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.execute
import com.jayrave.falkon.sqlBuilders.test.findAllRecordsInTable
import org.assertj.core.api.Assertions.assertThat
import java.sql.Types

class TestInsertOrReplace(
        private val insertOrReplaceSqlBuilder: InsertOrReplaceSqlBuilder,
        private val db: DbForTest) {

    private val dataSource = db.dataSource


    fun `new records are inserted via insert or replace with both null & non null values`() {
        val tableName = "test"
        val intColumnName = "int"
        val stringColumnName = "string"
        val idColumns = listOf(intColumnName)
        val nonIdColumns = listOf(stringColumnName)

        // Create table
        dataSource.execute(
                "CREATE TABLE $tableName (" +
                        "$intColumnName ${db.intDataType} PRIMARY KEY, " +
                        "$stringColumnName ${db.stringDataType})"
        )

        // Build sql
        val sql = insertOrReplaceSqlBuilder.build(tableName, idColumns, nonIdColumns)

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
                tableName, listOf(intColumnName, stringColumnName)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecords).hasSize(2)
        assertThat(allRecords[0]).containsEntry(intColumnName, 5.toString())
        assertThat(allRecords[0]).containsEntry(stringColumnName, "test 6")
        assertThat(allRecords[1]).containsEntry(intColumnName, 7.toString())
        assertThat(allRecords[1]).containsEntry(stringColumnName, null)
    }


    fun `existing records are updated via insert or replace with both null & non null values`() {
        val tableName = "test"
        val intColumnName = "int"
        val stringColumnName = "string"
        val idColumns = listOf(intColumnName)
        val nonIdColumns = listOf(stringColumnName)

        // Create table
        dataSource.execute(
                "CREATE TABLE $tableName (" +
                        "$intColumnName ${db.intDataType} PRIMARY KEY, " +
                        "$stringColumnName ${db.stringDataType})"
        )

        // Insert records
        dataSource.execute("INSERT INTO $tableName VALUES(${buildArgListForSql(5, "test 6")})")
        dataSource.execute("INSERT INTO $tableName VALUES(${buildArgListForSql(7, null)})")

        // Query for all records
        val allRecords = dataSource.findAllRecordsInTable(
                tableName, listOf(intColumnName, stringColumnName)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecords).hasSize(2)
        assertThat(allRecords[0]).containsEntry(intColumnName, 5.toString())
        assertThat(allRecords[0]).containsEntry(stringColumnName, "test 6")
        assertThat(allRecords[1]).containsEntry(intColumnName, 7.toString())
        assertThat(allRecords[1]).containsEntry(stringColumnName, null)

        // Build sql
        val sql = insertOrReplaceSqlBuilder.build(tableName, idColumns, nonIdColumns)

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
                tableName, listOf(intColumnName, stringColumnName)
        )

        // Assert that the inserted values were persisted
        assertThat(allRecordsAfterUpdate).hasSize(2)
        assertThat(allRecordsAfterUpdate[0]).containsEntry(intColumnName, 5.toString())
        assertThat(allRecordsAfterUpdate[0]).containsEntry(stringColumnName, null)
        assertThat(allRecordsAfterUpdate[1]).containsEntry(intColumnName, 7.toString())
        assertThat(allRecordsAfterUpdate[1]).containsEntry(stringColumnName, "test 8")
    }



    fun `can insert or replace into table with only id columns`() {
        val tableName = "test"
        val intColumnName = "int"
        val idColumns = listOf(intColumnName)

        // Create table
        dataSource.execute("CREATE TABLE $tableName ($intColumnName ${db.intDataType} PRIMARY KEY)")

        // Build sql
        val sql = insertOrReplaceSqlBuilder.build(tableName, idColumns, emptyList())

        // Insert record
        dataSource.execute(sql) { ps ->
            ps.setInt(1, 5)
        }

        // Query for all records
        val allRecordsOnInsert = dataSource.findAllRecordsInTable(tableName, listOf(intColumnName))

        // Assert that the inserted values were persisted
        assertThat(allRecordsOnInsert).hasSize(1)
        assertThat(allRecordsOnInsert[0]).containsEntry(intColumnName, 5.toString())

        // Update record
        dataSource.execute(sql) { ps ->
            ps.setInt(1, 5)
        }

        // Query for all records
        val allRecordsOnReplace = dataSource.findAllRecordsInTable(tableName, listOf(intColumnName))

        // Assert that the record is still present
        assertThat(allRecordsOnReplace).hasSize(1)
        assertThat(allRecordsOnReplace[0]).containsEntry(intColumnName, 5.toString())
    }
}