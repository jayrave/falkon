package com.jayrave.falkon.dao.where

import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.assertWhereEquality
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.*
import org.junit.Test
import com.jayrave.falkon.dao.where.lenient.AdderOrEnder as LenientAdderOrEnder
import com.jayrave.falkon.dao.where.lenient.WhereBuilderImpl as LenienWhereBuilderImpl

/**
 * As of July 17, 2016, [com.jayrave.falkon.dao.where.lenient.WhereBuilderImpl] is being used
 * by [WhereBuilderImpl] under the hood. Therefore, this test class has only a few smoke tests
 * that touches almost all the functionality of [WhereBuilderImpl]
 */
class WhereBuilderImplTest {

    private val table = TableForTest()
    private lateinit var builder: WhereBuilderImpl<ModelForTest, AdderOrEnderForTest>


    @Test
    fun testWhereWithAllSectionsWithoutBackingImplementationInjection() {
        builder = WhereBuilderImpl({ AdderOrEnderForTest(it) })
        performTestWhereWithAllSections(false)
    }


    @Test
    fun testWhereWithAllSectionsWithBackingImplementationInjection() {
        val lenientWhereBuilderImpl = LenienWhereBuilderImpl<LenientAdderOrEnderForTest>(true) {
            LenientAdderOrEnderForTest(it)
        }

        builder = WhereBuilderImpl({ AdderOrEnderForTest(it) }, lenientWhereBuilderImpl)
        performTestWhereWithAllSections(true)
    }


    private fun performTestWhereWithAllSections(qualifiedColumnNames: Boolean) {
        val actualWhere = builder
                .eq(table.short, 5).or()
                .notEq(table.int, 6).and()
                .gt(table.nullableInt, 7).or()
                .le(table.float, 8f).and()
                .or {
                    between(table.double, 9.0, 10.0)
                    ge(table.string, "test 1")
                }.or()
                .and {
                    lt(table.blob, byteArrayOf(11))
                    like(table.long, "12")
                }.and()
                .isIn(table.nullableFloat, 13F, 14F, 15F).or()
                .isNotIn(table.nullableDouble, 16.0).and()
                .isNull(table.nullableInt).or()
                .isNotNull(table.int)
                .build()

        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.short.getAppropriateName(qualifiedColumnNames)),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table.int.getAppropriateName(qualifiedColumnNames)),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.getAppropriateName(qualifiedColumnNames)),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, table.float.getAppropriateName(qualifiedColumnNames)),
                        SimpleConnector(SimpleConnector.Type.AND),
                        CompoundConnector(
                                CompoundConnector.Type.OR,
                                listOf(
                                        BetweenPredicate(table.double.getAppropriateName(qualifiedColumnNames)),
                                        OneArgPredicate(
                                                OneArgPredicate.Type.GREATER_THAN_OR_EQ, table.string.getAppropriateName(qualifiedColumnNames)
                                        )
                                )
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        CompoundConnector(
                                CompoundConnector.Type.AND,
                                listOf(
                                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table.blob.getAppropriateName(qualifiedColumnNames)),
                                        OneArgPredicate(OneArgPredicate.Type.LIKE, table.long.getAppropriateName(qualifiedColumnNames))
                                )
                        ), SimpleConnector(SimpleConnector.Type.AND),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_IN, table.nullableFloat.getAppropriateName(qualifiedColumnNames), 3),
                        SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, table.nullableDouble.getAppropriateName(qualifiedColumnNames), 1),
                        SimpleConnector(SimpleConnector.Type.AND),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, table.nullableInt.getAppropriateName(qualifiedColumnNames)),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, table.int.getAppropriateName(qualifiedColumnNames))
                ),
                listOf(
                        5, 6, 7, 8f, 9.0, 10.0, "test 1", byteArrayOf(11), "12",
                        13F, 14F, 15F, 16.0
                )
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }



    private inner class AdderOrEnderForTest(
            private val delegate: AdderOrEnder<ModelForTest, AdderOrEnderForTest>) :
            AdderOrEnder<ModelForTest, AdderOrEnderForTest> {

        fun build() = builder.build()
        override fun and() = delegate.and()
        override fun or() = delegate.or()
    }



    private inner class LenientAdderOrEnderForTest(
            private val delegate: LenientAdderOrEnder<LenientAdderOrEnderForTest>) :
            LenientAdderOrEnder<LenientAdderOrEnderForTest> {

        override fun and() = delegate.and()
        override fun or() = delegate.or()
    }



    companion object {
        private fun Column<*, *>.getAppropriateName(qualifyColumnName: Boolean): String {
            return when (qualifyColumnName) {
                true -> qualifiedName
                else -> name
            }
        }
    }
}