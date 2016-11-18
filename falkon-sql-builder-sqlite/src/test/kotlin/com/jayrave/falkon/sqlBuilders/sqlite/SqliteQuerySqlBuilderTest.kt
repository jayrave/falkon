package com.jayrave.falkon.sqlBuilders.sqlite

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SqliteQuerySqlBuilderTest {

    private val querySqlBuilder = SqliteQuerySqlBuilder()

    @Test
    fun testWithoutLimitAndOffset() {
        val actualSql = callBuildQuerySqlFromParts()
        val expectedSql = "SELECT * FROM $TABLE_NAME"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testLimitWithoutOffset() {
        val actualSql = callBuildQuerySqlFromParts(limit = 5)
        val expectedSql = "SELECT * FROM $TABLE_NAME LIMIT 5"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testLimitWithOffset() {
        val actualSql = callBuildQuerySqlFromParts(limit = 5, offset = 2)
        val expectedSql = "SELECT * FROM $TABLE_NAME LIMIT 5 OFFSET 2"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testOffsetWithoutLimit() {
        val actualSql = callBuildQuerySqlFromParts(offset = 5)

        // Since sqlite's offset can't exist without limit, it should be artificially injected
        val expectedSql = "SELECT * FROM $TABLE_NAME LIMIT -1 OFFSET 5"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    private fun callBuildQuerySqlFromParts(
            tableName: String = TABLE_NAME, limit: Long? = null, offset: Long? = null): String {

        return querySqlBuilder.build(
                tableName = tableName, distinct = false, columns = null,
                joinInfos = null, whereSections = null, groupBy = null,
                orderBy = null, limit = limit, offset = offset
        )
    }



    companion object {
        private const val TABLE_NAME = "test"
    }
}