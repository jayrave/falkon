package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.DbForTest
import com.jayrave.falkon.sqlBuilders.test.buildArgListForSql
import com.jayrave.falkon.sqlBuilders.test.failIfOpDoesNotThrow
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestMaxSize(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `max sized column does not allow values that are too big`() {

        // Create table with a max sized column
        val columnInfo = ColumnInfoForTest("max_sized_column", db.stringDataType, maxSize = 50)
        val tableInfo = TableInfoForTest("test", listOf(columnInfo), emptyList(), emptyList())
        db.execute(createTableSqlBuilder.build(tableInfo))

        // Insert SQL builder
        fun buildInsertSql(value: String?): String {
            return "INSERT INTO ${tableInfo.name} VALUES (${buildArgListForSql(value)})"
        }

        // Make sure records can be inserted
        db.execute(buildInsertSql(randomStringGenerator(columnInfo.maxSize!! - 1)))
        assertThat(db.findRecordCountInTable(tableInfo.name)).isEqualTo(1)

        // Try inserting a max size constraint violating value
        failIfOpDoesNotThrow {
            db.execute(buildInsertSql(randomStringGenerator(columnInfo.maxSize * 2)))
        }
    }


    companion object {
        private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"
        private val random = Random()
        private fun randomStringGenerator(size: Int): String {
            require(size >= 1)
            return (0..size - 1).fold(initial = StringBuilder(size)) { stringBuilder, unused ->
                stringBuilder.append(ALPHABET[random.nextInt(ALPHABET.length)])
            }.toString()
        }
    }
}