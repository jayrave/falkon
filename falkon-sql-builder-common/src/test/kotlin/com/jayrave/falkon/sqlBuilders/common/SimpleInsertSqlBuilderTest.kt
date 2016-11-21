package com.jayrave.falkon.sqlBuilders.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleInsertSqlBuilderTest {

    private val tableName = "test"

    @Test(expected = SQLSyntaxErrorException::class)
    fun `build throws for empty columns iterable`() {
        SimpleInsertSqlBuilder.build("test", emptyList())
    }


    @Test
    fun `successfully builds`() {
        val actualSql = SimpleInsertSqlBuilder.build(
                tableName, listOf("column_name_1", "column_name_2")
        )

        val expectedSql = "INSERT INTO $tableName (column_name_1, column_name_2) VALUES (?, ?)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }
}