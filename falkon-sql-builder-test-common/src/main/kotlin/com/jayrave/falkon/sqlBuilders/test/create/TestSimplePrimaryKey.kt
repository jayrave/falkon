package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestSimplePrimaryKey(createTableSqlBuilder: CreateTableSqlBuilder, db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        val idColumnInfo = ColumnInfoForTest("id", dataType = db.stringDataType, isId = true)
        val tableInfo = TableInfoForTest(TABLE_NAME, listOf(idColumnInfo), emptyList(), emptyList())
        dataSource.execute(createTableSqlBuilder.build(tableInfo))
    }


    fun `simple primary key does not allow duplicates`() {
        // Make sure records can be inserted
        val idToBeDuplicated = randomUuid()
        dataSource.execute(buildInsertSql(idToBeDuplicated))
        assertRowCountInTable(1)

        // Try inserting another row with the same id. It should throw
        failIfOpDoesNotThrow {
            dataSource.execute(buildInsertSql(idToBeDuplicated))
        }
    }


    fun `simple primary key cannot be null`() {
        // Make sure records can be inserted
        dataSource.execute(buildInsertSql(randomUuid()))
        assertRowCountInTable(1)

        // Try inserting row with null id. It should throw
        failIfOpDoesNotThrow {
            dataSource.execute(buildInsertSql(null))
        }
    }


    private fun assertRowCountInTable(expected: Int) {
        assertThat(dataSource.findRecordCountInTable(TABLE_NAME)).isEqualTo(expected)
    }



    companion object {
        private const val TABLE_NAME = "test"
        fun buildInsertSql(idValue: UUID?): String {
            return when (idValue) {
                null -> "INSERT INTO $TABLE_NAME VALUES (${buildArgListForSql(null)})"
                else -> "INSERT INTO $TABLE_NAME VALUES (${buildArgListForSql(idValue)})"
            }
        }
    }
}