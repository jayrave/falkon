package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.OneArgPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeleteKtTest {

    private val tableName = "test"

    @Test
    fun testBuildDeleteSqlFromPartsWithoutWhere() {
        val actualSql = buildDeleteSqlFromParts(tableName, null)
        val expectedSql = "DELETE FROM $tableName"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildDeleteSqlFromPartsWithWhere() {
        val whereSections = listOf(
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                SimpleConnector(SimpleConnector.Type.AND),
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
        )

        val actualSql = buildDeleteSqlFromParts(tableName, whereSections)
        val expectedSql = "DELETE FROM $tableName WHERE column_name_1 = ? AND column_name_2 = ?"
        assertThat(actualSql).isEqualTo(expectedSql)
    }
}