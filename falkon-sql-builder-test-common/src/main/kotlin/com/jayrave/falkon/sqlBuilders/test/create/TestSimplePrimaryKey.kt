package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.failIfOpDoesNotThrow
import com.jayrave.falkon.sqlBuilders.test.randomUuid
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestSimplePrimaryKey(
        createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    init {
        val idColumnInfo = ColumnInfoForTest("id", dataType = db.stringDataType, isId = true)
        val tableInfo = TableInfoForTest(TABLE_NAME, listOf(idColumnInfo), emptyList(), emptyList())
        db.execute(createTableSqlBuilder.build(tableInfo))
    }


    fun `simple primary key does not allow duplicates`() {
        // Make sure records can be inserted
        val idToBeDuplicated = randomUuid()
        db.execute(buildInsertSql(idToBeDuplicated))
        assertRowCountInTable(1)

        // Try inserting another row with the same id. It should throw
        failIfOpDoesNotThrow {
            db.execute(buildInsertSql(idToBeDuplicated))
        }
    }


    fun `simple primary key cannot be null`() {
        // Make sure records can be inserted
        db.execute(buildInsertSql(randomUuid()))
        assertRowCountInTable(1)

        // Try inserting row with null id. It should throw
        failIfOpDoesNotThrow {
            db.execute(buildInsertSql(null))
        }
    }


    private fun assertRowCountInTable(expected: Int) {
        assertThat(db.findRecordCountInTable(TABLE_NAME)).isEqualTo(expected)
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