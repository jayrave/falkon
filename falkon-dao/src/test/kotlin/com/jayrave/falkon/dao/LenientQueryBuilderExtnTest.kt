package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.query.lenient.AdderOrEnder
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.mapper.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException

class LenientQueryBuilderExtnTest {

    @Test(expected = SQLException::class)
    fun testSelectWithEmptyListThrows() {
        AdderOrEnderForTest().select(listOf())
    }


    @Test
    fun testSelectWithOneArgList() {
        val table = TableForTest()
        val adderOrEnderForTest = AdderOrEnderForTest()
        adderOrEnderForTest.select(listOf(table.int))

        assertThat(adderOrEnderForTest.firstColumn).isSameAs(table.int)
        assertThat(adderOrEnderForTest.remainingColumns).isNullOrEmpty()
    }


    // TODO - Causes compilation errors (with kotlin 1.0.3)
//    @Test
//    fun testSelectWithMultiArgList() {
//        val table = TableForTest()
//        val adderOrEnderForTest = AdderOrEnderForTest()
//        adderOrEnderForTest.select(listOf(table.int, table.nullableString, table.blob))
//
//        assertThat(adderOrEnderForTest.firstColumn).isSameAs(table.int)
//        assertThat(adderOrEnderForTest.remainingColumns).containsExactlyElementsOf(
//                listOf(table.nullableString, table.blob)
//        )
//    }


    /**
     * Stores select arguments. Throws for everything else
     */
    internal class AdderOrEnderForTest : AdderOrEnder<AdderOrEnderForTest> {

        var firstColumn: Column<*, *>? = null
        var remainingColumns: List<Column<*, *>>? = null

        override fun select(column: Column<*, *>, vararg others: Column<*, *>):
                AdderOrEnderForTest {

            firstColumn = column
            remainingColumns = others.toList()
            return this
        }

        private fun exception() = UnsupportedOperationException("not implemented")
        override fun distinct() = throw exception()
        override fun join(column: Column<*, *>, onColumn: Column<*, *>) = throw exception()
        override fun groupBy(column: Column<*, *>, vararg others: Column<*, *>) = throw exception()
        override fun orderBy(column: Column<*, *>, ascending: Boolean) = throw exception()
        override fun limit(count: Long) = throw exception()
        override fun offset(count: Long) = throw exception()
        override fun build() = throw exception()
        override fun compile() = throw exception()
    }
}