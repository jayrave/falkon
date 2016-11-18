package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat

class TestGroupBy(
        val querySqlBuilder: QuerySqlBuilder,
        val db: DbForTest) {

    fun `select with group by`() {
        val tableName = "test"
        val intColumnName = "int"
        val dataSource = db.dataSource

        dataSource.execute("CREATE TABLE $tableName ($intColumnName ${db.intDataType})")

        fun insertRecord(int: Int) {
            dataSource.execute("INSERT INTO $tableName VALUES(${buildArgListForSql(int)})")
        }

        // Insert records
        insertRecord(5)
        insertRecord(6)
        insertRecord(6)
        insertRecord(7)
        insertRecord(7)
        insertRecord(7)

        val sql = querySqlBuilder.build(
                tableName, false, null, null, null, listOf(intColumnName), null, null, null
        )

        val allRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(intColumnName))
        }

        assertThat(allRecords).hasSize(3)

        val allValues = allRecords.map { it[intColumnName] }
        assertThat(allValues).containsOnly("5", "6", "7")
    }
}