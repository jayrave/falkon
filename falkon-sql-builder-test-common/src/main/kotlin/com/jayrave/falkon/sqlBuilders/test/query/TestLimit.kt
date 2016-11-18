package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestLimit(
        private val querySqlBuilder: QuerySqlBuilder,
        private val db: DbForTest) {

    fun `select with limit`() {
        // Create table
        val dataSource = db.dataSource
        val tableName = "test"
        val intColumnName = "int"
        dataSource.execute("CREATE TABLE $tableName ($intColumnName ${db.intDataType})")

        fun insertRecord(int: Int) {
            dataSource.execute("INSERT INTO $tableName VALUES(${buildArgListForSql(int)})")
        }

        // Insert records out of order
        insertRecord(5)
        insertRecord(6)
        insertRecord(7)

        val sql = querySqlBuilder.build(tableName, false, null, null, null, null, null, 1, null)
        val allRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(intColumnName))
        }

        assertThat(allRecords).hasSize(1)
    }
}