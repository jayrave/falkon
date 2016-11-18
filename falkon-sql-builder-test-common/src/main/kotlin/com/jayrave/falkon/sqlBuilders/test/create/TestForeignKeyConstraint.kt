package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class TestForeignKeyConstraint(
        private val createTableSqlBuilder: CreateTableSqlBuilder,
        private val db: DbForTest) {

    fun `foreign key constraint violating operation isn't allowed`() {
        val dataSource = db.dataSource

        // Build foreign table info
        val columnInfoFromForeignTable = ColumnInfoForTest("id", db.stringDataType, isId = true)
        val foreignTableInfo = TableInfoForTest(
                "foreign_table", listOf(columnInfoFromForeignTable), emptyList(), emptyList()
        )

        // Build table info with foreign column
        val idColumnFromReferringTable = ColumnInfoForTest("id", db.stringDataType, isId = true)
        val foreignIdColumnFromReferringTable = ColumnInfoForTest("f_id", db.stringDataType)
        val referringTableInfo = TableInfoForTest(
                "test", listOf(idColumnFromReferringTable, foreignIdColumnFromReferringTable),
                emptyList(),
                listOf(ForeignKeyConstraintForTest(
                        foreignIdColumnFromReferringTable.name, foreignTableInfo.name,
                        columnInfoFromForeignTable.name
                ))
        )

        // Create tables
        dataSource.execute(createTableSqlBuilder.build(foreignTableInfo))
        dataSource.execute(createTableSqlBuilder.build(referringTableInfo))

        fun buildInsertSqlForForeignTable(value: UUID): String {
            return "INSERT INTO ${foreignTableInfo.name} VALUES (${buildArgListForSql(value)})"
        }

        fun buildInsertSqlForReferringTable(id: UUID, foreignId: UUID): String {
            return "INSERT INTO ${referringTableInfo.name} " +
                    "VALUES (${buildArgListForSql(id, foreignId)})"
        }

        // Insert records into foreign table
        val id1FromForeignTable = randomUuid()
        val id2FromForeignTable = randomUuid()
        dataSource.execute(buildInsertSqlForForeignTable(id1FromForeignTable))
        dataSource.execute(buildInsertSqlForForeignTable(id2FromForeignTable))
        assertThat(dataSource.findRecordCountInTable(foreignTableInfo.name)).isEqualTo(2)

        // Insert valid records into referring table
        dataSource.execute(buildInsertSqlForReferringTable(randomUuid(), id1FromForeignTable))
        dataSource.execute(buildInsertSqlForReferringTable(randomUuid(), id2FromForeignTable))
        dataSource.execute(buildInsertSqlForReferringTable(randomUuid(), id1FromForeignTable))
        assertThat(dataSource.findRecordCountInTable(referringTableInfo.name)).isEqualTo(3)

        // Try inserting a record with an invalid foreign id. It should throw
        failIfOpDoesNotThrow {
            dataSource.execute(buildInsertSqlForReferringTable(randomUuid(), randomUuid()))
        }
    }
}