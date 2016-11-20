package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.mapper.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException
import java.util.*

class QueryBuilderExtnTest {

    @Test(expected = SQLException::class)
    fun testSelectWithEmptyListThrows() {
        AdderOrEnderForTest().select(listOf())
    }


    @Test
    fun testSelectWithOneArgList() {
        val table = TableForTest()
        val adderOrEnderForTest = AdderOrEnderForTest()
        adderOrEnderForTest.select(listOf(table.int))

        assertThat(adderOrEnderForTest.columns).containsOnly(table.int)
    }


    @Test
    fun testSelectWithMultiArgList() {
        val table = TableForTest()
        val adderOrEnderForTest = AdderOrEnderForTest()
        adderOrEnderForTest.select(listOf(table.int, table.nullableString, table.blob))

        assertThat(adderOrEnderForTest.columns).containsOnly(
                table.int, table.nullableString, table.blob
        )
    }


    /**
     * Stores select arguments. Throws for everything else
     */
    internal class AdderOrEnderForTest : AdderOrEnder<ModelForTest, AdderOrEnderForTest> {

        var columns = ArrayList<Column<*, *>>()

        override fun select(
                column: Column<ModelForTest, *>, vararg others: Column<ModelForTest, *>):
                AdderOrEnderForTest {

            columns.add(column)
            columns.addAll(others)
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