package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import org.assertj.core.api.Assertions.assertThat

class TestAutoIncrement(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `auto incrementing column increments value by at least 1 if not explicitly inserted`() {

        // Create table with auto incrementing primary key
        val counterColumn = ColumnInfoForTest("counter", db.intDataType)
        val idColumn = ColumnInfoForTest("id", db.intDataType, isId = true, autoIncrement = true)
        val tableInfo = TableInfoForTest(
                "test", listOf(idColumn, counterColumn), emptyList(), emptyList()
        )

        db.execute(createTableSqlBuilder.build(tableInfo))

        // Insert records with increasing counter
        val counter = 1..5
        counter.forEach {
            db.execute(
                    "INSERT INTO ${tableInfo.name} (${counterColumn.name}) " +
                            "VALUES (${buildArgListForSql(it)})"
            )
        }

        // Get all records from db
        val allRecords = db.findAllRecordsInTable(
                tableInfo.name, listOf(idColumn.name, counterColumn.name)
        )

        // Make sure all records got inserted
        assertThat(allRecords).hasSize(counter.count())

        // Assert auto increment works
        allRecords.take(allRecords.size - 1).forEachIndexed { index, currentRecord ->
            // Extract fields of current record
            val currentRecordId = currentRecord[idColumn.name]!!.toInt()
            val currentRecordCounter = currentRecord[counterColumn.name]!!.toInt()

            // Extract fields of next record
            val nextRecord = allRecords[index + 1]
            val nextRecordId = nextRecord[idColumn.name]!!.toInt()
            val nextRecordCounter = nextRecord[counterColumn.name]!!.toInt()

            assertThat(Math.signum(currentRecordId.compareTo(nextRecordId).toFloat())).isEqualTo(
                    Math.signum(currentRecordCounter.compareTo(nextRecordCounter).toFloat())
            )
        }
    }
}