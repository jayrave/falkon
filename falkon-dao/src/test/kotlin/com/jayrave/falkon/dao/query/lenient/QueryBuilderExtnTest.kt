package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.query.JoinType
import com.jayrave.falkon.dao.query.testLib.SelectColumnInfoForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.mapper.ReadOnlyColumnOfTable
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
        val aliaser = { column: ReadOnlyColumnOfTable<*, *> -> "${column.name}-aliased" }
        adderOrEnderForTest.select(listOf(table.int, table.blob), aliaser)

        assertThat(adderOrEnderForTest.selectColumnInfoList).containsOnly(
                SelectColumnInfoForTest(table.int.name, aliaser.invoke(table.int)),
                SelectColumnInfoForTest(table.blob.name, aliaser.invoke(table.blob))
        )
    }


    /**
     * Stores select arguments. Throws for everything else (raw select throws too)
     */
    internal class AdderOrEnderForTest : AdderOrEnder<AdderOrEnderForTest> {

        var selectColumnInfoList = ArrayList<SelectColumnInfo>()

        override fun select(
                column: ReadOnlyColumnOfTable<*, *>, alias: String?):
                AdderOrEnderForTest {

            selectColumnInfoList.add(SelectColumnInfoForTest(column.name, alias))
            return this
        }

        private fun exception() = UnsupportedOperationException("not implemented")
        override fun distinct() = throw exception()
        override fun select(column: String, alias: String?) = throw exception()
        override fun limit(count: Long) = throw exception()
        override fun offset(count: Long) = throw exception()
        override fun build() = throw exception()
        override fun compile() = throw exception()
        override fun join(
                column: ReadOnlyColumnOfTable<*, *>, onColumn: ReadOnlyColumnOfTable<*, *>,
                joinType: JoinType
        ) = throw exception()

        override fun groupBy(
                column: ReadOnlyColumnOfTable<*, *>,
                vararg others: ReadOnlyColumnOfTable<*, *>
        ) = throw exception()

        override fun orderBy(
                column: ReadOnlyColumnOfTable<*, *>, ascending: Boolean
        ) = throw exception()
    }
}