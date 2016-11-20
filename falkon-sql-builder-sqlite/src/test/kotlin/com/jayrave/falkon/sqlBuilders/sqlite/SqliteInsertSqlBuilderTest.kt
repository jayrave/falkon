package com.jayrave.falkon.sqlBuilders.sqlite

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class SqliteInsertSqlBuilderTest {

    @Test
    fun `insert`() {
        val tableName = "test"
        val actualSql = SqliteInsertSqlBuilder().build(tableName, listOf("column_1", "column_2"))
        val expectedSql = "INSERT INTO $tableName (column_1, column_2) VALUES (?, ?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun `insert or replace`() {
        val tableName = "test"
        val actualSql = SqliteInsertSqlBuilder().buildInsertOrReplace(
                tableName, listOf("id_column_1"), listOf("non_id_column_1")
        )

        val expectedSql = "INSERT OR REPLACE INTO $tableName " +
                "(id_column_1, non_id_column_1) " +
                "VALUES (?, ?)"

        assertThat(actualSql.sql).isEqualTo(expectedSql)
        assertThat(actualSql.indexToIndicesMap.size).isEqualTo(2)
        assertThat(Arrays.equals(
                actualSql.indexToIndicesMap.indicesForIndex(1), intArrayOf(1)
        )).isTrue()

        assertThat(Arrays.equals(
                actualSql.indexToIndicesMap.indicesForIndex(2), intArrayOf(2)
        )).isTrue()
    }
}