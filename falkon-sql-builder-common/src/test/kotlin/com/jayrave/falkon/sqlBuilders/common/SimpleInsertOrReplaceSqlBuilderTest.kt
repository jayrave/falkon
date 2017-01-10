package com.jayrave.falkon.sqlBuilders.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleInsertOrReplaceSqlBuilderTest {

    private val tableName = "test"

    @Test(expected = SQLSyntaxErrorException::class)
    fun `build insert or replace throws for empty id columns iterable`() {
        SimpleInsertOrReplaceSqlBuilder.build("I_O_R", "test", emptyList(), listOf("a"))
    }


    @Test
    fun `successfully builds`() {
        val actualSql = SimpleInsertOrReplaceSqlBuilder.build(
                "I_O_R", tableName, listOf("id_column_1"), listOf("non_id_column_1")
        )

        val expectedSql = "I_O_R INTO $tableName (id_column_1, non_id_column_1) VALUES (?, ?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun `successfully builds with empty non id columns iterable`() {
        val actualSql = SimpleInsertOrReplaceSqlBuilder.build(
                "I_O_R", "test", listOf("a"), emptyList()
        )

        val expectedSql = "I_O_R INTO $tableName (a) VALUES (?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }
}