package com.jayrave.falkon.engine

import com.jayrave.falkon.sqlBuilders.SimpleDeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SimpleDeleteSqlBuilderTest {

    private val tableName = "test"

    @Test
    fun testBuildWithoutWhere() {
        val actualSql = SimpleDeleteSqlBuilder().build(tableName, null, ARG_PLACEHOLDER)
        val expectedSql = "DELETE FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildWithWhere() {
        val whereSections = listOf(
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                SimpleConnector(SimpleConnector.Type.AND),
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
        )

        val actualSql = SimpleDeleteSqlBuilder().build(tableName, whereSections, ARG_PLACEHOLDER)
        val expectedSql = "DELETE FROM $tableName WHERE column_name_1 = ? AND column_name_2 = ?"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    companion object {
        private const val ARG_PLACEHOLDER = "?"
    }
}