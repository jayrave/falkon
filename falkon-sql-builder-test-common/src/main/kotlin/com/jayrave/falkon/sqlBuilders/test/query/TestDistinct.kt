package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestDistinct(
        private val querySqlBuilder: QuerySqlBuilder,
        db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        dataSource.execute("CREATE TABLE $TABLE_NAME ($INT_COLUMN_NAME ${db.intDataType})")

        // Insert records
        insertRecord(5)
        insertRecord(6)
        insertRecord(6)
        insertRecord(7)
        insertRecord(7)
    }


    fun `select with duplicates`() {
        val sql = querySqlBuilder.build(TABLE_NAME, false, null, null, null, null, null, null, null)
        val allRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(INT_COLUMN_NAME))
        }

        assertThat(allRecords).hasSize(5)

        val allValues = allRecords.map { it[INT_COLUMN_NAME] }
        assertThat(allValues).containsOnly("5", "6", "6", "7", "7")
    }


    fun `select distinct`() {
        val sql = querySqlBuilder.build(TABLE_NAME, true, null, null, null, null, null, null, null)
        val allRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(INT_COLUMN_NAME))
        }

        assertThat(allRecords).hasSize(3)

        val allValues = allRecords.map { it[INT_COLUMN_NAME] }
        assertThat(allValues).containsOnly("5", "6", "7")
    }


    private fun insertRecord(int: Int) {
        dataSource.execute("INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(int)})")
    }



    companion object {
        private const val TABLE_NAME = "test"
        private const val INT_COLUMN_NAME = "int"
    }
}