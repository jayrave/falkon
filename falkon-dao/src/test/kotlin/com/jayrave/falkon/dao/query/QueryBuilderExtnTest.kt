package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.query.testLib.SelectColumnInfoForTest
import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException
import java.util.*

class QueryBuilderExtnTest {

    @Test(expected = SQLException::class)
    fun `select with empty list throws`() {
        AdderOrEnderForTest().select(listOf())
    }


    @Test
    fun `select with one arg list`() {
        val table = TableForTest()
        val adderOrEnderForTest = AdderOrEnderForTest()
        adderOrEnderForTest.select(listOf(table.int))

        assertThat(adderOrEnderForTest.selectColumnInfoList).containsOnly(
                SelectColumnInfoForTest(table.int.name, null)
        )
    }


    @Test
    fun `select with multi arg list`() {
        val table = TableForTest()
        val adderOrEnderForTest = AdderOrEnderForTest()
        adderOrEnderForTest.select(listOf(table.int, table.nullableString, table.blob))

        assertThat(adderOrEnderForTest.selectColumnInfoList).containsOnly(
                SelectColumnInfoForTest(table.int.name, null),
                SelectColumnInfoForTest(table.nullableString.name, null),
                SelectColumnInfoForTest(table.blob.name, null)
        )
    }


    @Test
    fun `select with multi arg list & aliaser`() {
        val table = TableForTest()
        val adderOrEnderForTest = AdderOrEnderForTest()
        val aliaserForTest = { column: Column<ModelForTest, *> -> "${column.name}-aliased" }
        adderOrEnderForTest.select(listOf(table.int, table.blob), aliaserForTest)

        assertThat(adderOrEnderForTest.selectColumnInfoList).containsOnly(
                SelectColumnInfoForTest(table.int.name, aliaserForTest.invoke(table.int)),
                SelectColumnInfoForTest(table.blob.name, aliaserForTest.invoke(table.blob))
        )
    }


    /**
     * Stores select arguments. Throws for everything else
     */
    internal class AdderOrEnderForTest : AdderOrEnder<ModelForTest, AdderOrEnderForTest> {

        var selectColumnInfoList = ArrayList<SelectColumnInfo>()

        override fun select(column: Column<*, *>, alias: String?): AdderOrEnderForTest {
            selectColumnInfoList.add(SelectColumnInfoForTest(column.name, alias))
            return this
        }

        private fun exception() = UnsupportedOperationException("not implemented")
        override fun distinct() = throw exception()
        override fun limit(count: Long) = throw exception()
        override fun offset(count: Long) = throw exception()
        override fun build() = throw exception()
        override fun compile() = throw exception()

        override fun join(
                column: Column<ModelForTest, *>, onColumn: Column<*, *>
        ) = throw exception()

        override fun groupBy(
                column: Column<ModelForTest, *>, vararg others: Column<ModelForTest, *>
        ) = throw exception()

        override fun orderBy(
                column: Column<ModelForTest, *>, ascending: Boolean
        ) = throw exception()
    }
}