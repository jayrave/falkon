package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat

class TestJoin(
        val querySqlBuilder: QuerySqlBuilder,
        val db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        dataSource.execute(
                "CREATE TABLE $FOREIGN_TABLE_NAME (" +
                        "$FOREIGN_TABLE_ID_COLUMN_NAME ${db.intDataType} PRIMARY KEY)"
        )

        dataSource.execute(
                "CREATE TABLE $PRIMARY_TABLE_NAME (" +
                        "$PRIMARY_TABLE_ID_COLUMN_NAME ${db.intDataType}, " +
                        "$PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME ${db.intDataType}, " +
                        "FOREIGN KEY ($PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME) " +
                        "REFERENCES $FOREIGN_TABLE_NAME($FOREIGN_TABLE_ID_COLUMN_NAME))"
        )

        // Insert records
        insertRecordInfoForeignTable(5)
        insertRecordInfoForeignTable(6)
        insertRecordInfoForeignTable(7)
        insertRecordInfoForeignTable(8)
        insertRecordInfoForeignTable(9)
        insertRecordInfoPrimaryTable(105, 5)
        insertRecordInfoPrimaryTable(106, 6)
        insertRecordInfoPrimaryTable(107, 7)
        insertRecordInfoPrimaryTable(110, null)
        insertRecordInfoPrimaryTable(111, null)
    }


    fun `select with inner join`() {
        val sql = buildSelectWithJoin(JoinInfo.Type.INNER_JOIN)
        val actualRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(
                    PRIMARY_TABLE_ID_COLUMN_NAME,
                    PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME,
                    FOREIGN_TABLE_ID_COLUMN_NAME
            ))
        }

        assertThat(actualRecords).containsOnly(
                buildJoinedRecord(105, 5, 5),
                buildJoinedRecord(106, 6, 6),
                buildJoinedRecord(107, 7, 7)
        )
    }


    fun `select with left outer join`() {
        val sql = buildSelectWithJoin(JoinInfo.Type.LEFT_OUTER_JOIN)
        val actualRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(
                    PRIMARY_TABLE_ID_COLUMN_NAME,
                    PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME,
                    FOREIGN_TABLE_ID_COLUMN_NAME
            ))
        }

        assertThat(actualRecords).containsOnly(
                buildJoinedRecord(105, 5, 5),
                buildJoinedRecord(106, 6, 6),
                buildJoinedRecord(107, 7, 7),
                buildJoinedRecord(110, null, null),
                buildJoinedRecord(111, null, null)
        )
    }


    fun `select with right outer join`() {
        val sql = buildSelectWithJoin(JoinInfo.Type.RIGHT_OUTER_JOIN)
        val actualRecords = dataSource.executeQuery(sql, {}) {
            it.extractRecordsAsMap(listOf(
                    PRIMARY_TABLE_ID_COLUMN_NAME,
                    PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME,
                    FOREIGN_TABLE_ID_COLUMN_NAME
            ))
        }

        assertThat(actualRecords).containsOnly(
                buildJoinedRecord(105, 5, 5),
                buildJoinedRecord(106, 6, 6),
                buildJoinedRecord(107, 7, 7),
                buildJoinedRecord(null, null, 8),
                buildJoinedRecord(null, null, 9)
        )
    }


    private fun buildJoinedRecord(
            primaryTableId: Int?, primaryTableForeignId: Int?, foreignTableId: Int?):
            Map<String, String?> {

        return mapOf(
                PRIMARY_TABLE_ID_COLUMN_NAME to primaryTableId?.toString(),
                PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME to primaryTableForeignId?.toString(),
                FOREIGN_TABLE_ID_COLUMN_NAME to foreignTableId?.toString()
        )
    }


    private fun insertRecordInfoForeignTable(id: Int) {
        dataSource.execute("INSERT INTO $FOREIGN_TABLE_NAME VALUES(${buildArgListForSql(id)})")
    }


    private fun insertRecordInfoPrimaryTable(id: Int, foreignId: Int?) {
        dataSource.execute("INSERT INTO $PRIMARY_TABLE_NAME " +
                "VALUES(${buildArgListForSql(id, foreignId)})")
    }


    private fun buildSelectWithJoin(joinType: JoinInfo.Type): String {
        // Since column names are unique, needn't use qualified names
        val joinInfo = JoinInfoForTest(
                joinType, PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME,
                FOREIGN_TABLE_NAME, FOREIGN_TABLE_ID_COLUMN_NAME
        )

        return querySqlBuilder.build(
                PRIMARY_TABLE_NAME, false, null, listOf(joinInfo), null, null, null, null, null
        )
    }


    companion object {
        private const val FOREIGN_TABLE_NAME = "foreign_table"
        private const val FOREIGN_TABLE_ID_COLUMN_NAME = "f_id"
        private const val PRIMARY_TABLE_NAME = "test"
        private const val PRIMARY_TABLE_ID_COLUMN_NAME = "p_id"
        private const val PRIMARY_TABLE_FOREIGN_ID_COLUMN_NAME = "p_foreign_id"
    }
}