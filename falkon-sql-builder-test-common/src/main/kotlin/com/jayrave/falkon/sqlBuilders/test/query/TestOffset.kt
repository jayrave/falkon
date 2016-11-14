package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestOffset(
        private val querySqlBuilder: QuerySqlBuilder,
        private val db: DbForTest) {

    fun `select with offset`() {
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

        val sql = querySqlBuilder.build(
                tableName, false, null, null, null, null,
                listOf(OrderInfoForTest(intColumnName, true)), null, 1
        )

        val allRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(intColumnName))
        }

        assertThat(allRecords).hasSize(2)
        val allValues = allRecords.map { it[intColumnName] }
        assertThat(allValues).containsExactly("6", "7") // 5 must have been skipped
    }
}