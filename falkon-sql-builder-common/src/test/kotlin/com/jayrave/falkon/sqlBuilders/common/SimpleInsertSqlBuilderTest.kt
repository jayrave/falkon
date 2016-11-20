package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.SqlAndIndexToIndicesMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException
import java.util.*

class SimpleInsertSqlBuilderTest {

    private val tableName = "test"

    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildThrowsForEmptyColumnsIterable() {
        SimpleInsertSqlBuilder.build("test", emptyList())
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildInsertOrReplaceThrowsForEmptyIdColumnsIterable() {
        SimpleInsertSqlBuilder.buildInsertOrReplace("I_O_R", "test", emptyList(), listOf("a"))
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildInsertOrReplaceThrowsForEmptyNonIdColumnsIterable() {
        SimpleInsertSqlBuilder.buildInsertOrReplace("I_O_R", "test", listOf("a"), emptyList())
    }


    @Test
    fun testSuccessfulBuild() {
        val actualSql = SimpleInsertSqlBuilder.build(
                tableName, listOf("column_name_1", "column_name_2")
        )

        val expectedSql = "INSERT INTO $tableName (column_name_1, column_name_2) VALUES (?, ?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testSuccessfulBuildInsertOrReplace() {
        val actualSqlAndIndexToIndicesMap = SimpleInsertSqlBuilder.buildInsertOrReplace(
                "I_O_R", tableName, listOf("id_column_1"), listOf("non_id_column_1")
        )

        val expectedSqlAndIndexToIndicesMap = SqlAndIndexToIndicesMap(
                sql = "I_O_R INTO $tableName (id_column_1, non_id_column_1) VALUES (?, ?)",
                indexToIndicesMap = SimpleIndexToIndicesMap(2)
        )

        assertThat(actualSqlAndIndexToIndicesMap.sql).isEqualTo(expectedSqlAndIndexToIndicesMap.sql)
        assertThat(actualSqlAndIndexToIndicesMap.indexToIndicesMap.size).isEqualTo(
                expectedSqlAndIndexToIndicesMap.indexToIndicesMap.size
        )

        (1..actualSqlAndIndexToIndicesMap.indexToIndicesMap.size).forEach { index ->
            assertThat(Arrays.equals(
                    actualSqlAndIndexToIndicesMap.indexToIndicesMap.indicesForIndex(index),
                    expectedSqlAndIndexToIndicesMap.indexToIndicesMap.indicesForIndex(index)
            )).isTrue()
        }
    }
}