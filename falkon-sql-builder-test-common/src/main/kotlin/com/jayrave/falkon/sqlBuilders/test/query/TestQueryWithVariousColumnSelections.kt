package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestQueryWithVariousColumnSelections(
        private val querySqlBuilder: QuerySqlBuilder,
        db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        dataSource.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$ID_COLUMN_NAME ${db.stringDataType} PRIMARY KEY, " +
                        "$INT_COLUMN_NAME ${db.intDataType}, " +
                        "$STRING_COLUMN_NAME ${db.stringDataType})"
        )
    }


    fun `select all columns without specifying any`() {
        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Run query
        val sql = querySqlBuilder.build(TABLE_NAME, false, null, null, null, null, null, null, null)
        val records = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(ID_COLUMN_NAME, INT_COLUMN_NAME, STRING_COLUMN_NAME))
        }

        // Assert all columns were selected
        assertThat(records).hasSize(2)
        records.forEach {
            assertThat(it).containsKeys(ID_COLUMN_NAME, INT_COLUMN_NAME, STRING_COLUMN_NAME)
        }
    }


    fun `select all columns by explicitly specifying every column`() {
        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Run query
        val columnInfos = listOf(
                SelectColumnInfoForTest(ID_COLUMN_NAME, null),
                SelectColumnInfoForTest(INT_COLUMN_NAME, null),
                SelectColumnInfoForTest(STRING_COLUMN_NAME, null)
        )

        val sql = querySqlBuilder.build(
                TABLE_NAME, false, columnInfos, null, null, null, null, null, null
        )

        val records = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(ID_COLUMN_NAME, INT_COLUMN_NAME, STRING_COLUMN_NAME))
        }

        // Assert all columns were selected
        assertThat(records).hasSize(2)
        records.forEach {
            assertThat(it).containsKeys(ID_COLUMN_NAME, INT_COLUMN_NAME, STRING_COLUMN_NAME)
        }
    }


    fun `select only a few columns by explicitly specifying it`() {
        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Run query
        val columnInfos = listOf(
                SelectColumnInfoForTest(ID_COLUMN_NAME, null),
                SelectColumnInfoForTest(INT_COLUMN_NAME, null)
        )

        val sql = querySqlBuilder.build(
                TABLE_NAME, false, columnInfos, null, null, null, null, null, null
        )

        // Assert only specified columns were selected
        dataSource.executeQuery(sql, {}) { resultSet ->
            assertThat(resultSet.metaData.columnCount).isEqualTo(2)
            resultSet.findColumn(ID_COLUMN_NAME) // Will throw if not in result set
            resultSet.findColumn(INT_COLUMN_NAME) // Will throw if not in result set
        }
    }


    fun `select columns by using alias`() {
        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Run query with alias for columns
        val columnNamePrefix = "asdf"
        val idAlias = "${columnNamePrefix}_$ID_COLUMN_NAME"
        val intAlias = "${columnNamePrefix}_$INT_COLUMN_NAME"
        val stringAlias = "${columnNamePrefix}_$STRING_COLUMN_NAME"
        val columnInfos = listOf(
                SelectColumnInfoForTest(ID_COLUMN_NAME, idAlias),
                SelectColumnInfoForTest(INT_COLUMN_NAME, intAlias),
                SelectColumnInfoForTest(STRING_COLUMN_NAME, stringAlias)
        )

        val sql = querySqlBuilder.build(
                TABLE_NAME, false, columnInfos, null, null, null, null, null, null
        )

        // Assert only specified columns were selected
        dataSource.executeQuery(sql, {}) { resultSet ->
            assertThat(resultSet.metaData.columnCount).isEqualTo(3)
            resultSet.findColumn(idAlias) // Will throw if not in result set
            resultSet.findColumn(intAlias) // Will throw if not in result set
            resultSet.findColumn(stringAlias) // Will throw if not in result set
        }
    }


    private fun insertRecord(id: UUID, int: Int?, string: String?) {
        dataSource.execute(
                "INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(id, int, string)})"
        )
    }



    companion object {
        private const val TABLE_NAME = "test"
        private const val ID_COLUMN_NAME = "id"
        private const val INT_COLUMN_NAME = "int"
        private const val STRING_COLUMN_NAME = "string"
    }
}