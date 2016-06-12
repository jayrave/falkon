package com.jayrave.falkon.dao.where

import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.engine.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.*
import com.jayrave.falkon.exceptions.SQLSyntaxErrorException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WhereBuilderImplTest {

    private val table = TableForTest()
    private val builder: WhereBuilderImpl<ModelForTest, AdderOrEnderForTest> = WhereBuilderImpl {
        AdderOrEnderForTest(it)
    }


    @Test
    fun testEmptyWhere() {
        val actualWhere = builder.build()
        val expectedWhere = Where(emptyList(), emptyList())
        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testEq() {
        val actualWhere = builder.eq(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testNotEq() {
        val actualWhere = builder.notEq(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testGt() {
        val actualWhere = builder.gt(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testGe() {
        val actualWhere = builder.ge(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLt() {
        val actualWhere = builder.lt(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLe() {
        val actualWhere = builder.le(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testBetween() {
        val actualWhere = builder.between(table.int, 5, 8).build()
        val expectedWhere = Where(listOf(BetweenPredicate("int")), listOf(5, 8))
        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLike() {
        val actualWhere = builder.like(table.int, "5").build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.LIKE, "int")), listOf("5")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNull() {
        val actualWhere = builder.isNull(table.int).build()
        val expectedWhere = Where(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NULL, "int")), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotNull() {
        val actualWhere = builder.isNotNull(table.int).build()
        val expectedWhere = Where(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "int")), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testSimpleAnd() {
        val actualWhere = builder.eq(table.int, 5).and().eq(table.string, "test").build()
        val expectedWhere = Where(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "string")
                ), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testSimpleOr() {
        val actualWhere = builder.eq(table.int, 5).or().eq(table.string, "test").build()
        val expectedWhere = Where(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "string")
                ), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testEmptyCompoundAndThrows() {
        builder.and {}.build()
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testEmptyCompoundOrThrows() {
        builder.or {}.build()
    }


    @Test
    fun testCompoundAnd() {
        val actualWhere = builder
                .and {
                    eq(table.int, 5)
                    eq(table.string, "test")
                }.build()

        val expectedWhere = Where(
                listOf(CompoundConnector(
                        CompoundConnector.Type.AND,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                                OneArgPredicate(OneArgPredicate.Type.EQ, "string")
                        )
                )), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testCompoundOr() {
        val actualWhere = builder
                .or {
                    eq(table.int, 5)
                    eq(table.string, "test")
                }.build()

        val expectedWhere = Where(
                listOf(CompoundConnector(
                        CompoundConnector.Type.OR,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                                OneArgPredicate(OneArgPredicate.Type.EQ, "string")
                        )
                )), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testWhereWithAllSections() {
        val actualWhere = builder
                .eq(table.short, 5).or()
                .notEq(table.int, 6).and()
                .gt(table.nullable, 7).or()
                .le(table.float, 8f).and()
                .or {
                    between(table.double, 9.0, 10.0)
                    ge(table.string, "test 1")
                }.or()
                .and {
                    lt(table.blob, byteArrayOf(11))
                    like(table.long, "12")
                }.and()
                .isNull(table.nullable).or()
                .isNotNull(table.int).build()

        val expectedWhere = Where(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "short"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "float"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        CompoundConnector(
                                CompoundConnector.Type.OR,
                                listOf(
                                        BetweenPredicate("double"),
                                        OneArgPredicate(
                                                OneArgPredicate.Type.GREATER_THAN_OR_EQ, "string"
                                        )
                                )
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        CompoundConnector(
                                CompoundConnector.Type.AND,
                                listOf(
                                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "blob"),
                                        OneArgPredicate(OneArgPredicate.Type.LIKE, "long")
                                )
                        ), SimpleConnector(SimpleConnector.Type.AND),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, "nullable"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "int")
                ), listOf(5, 6, 7, 8f, 9.0, 10.0, "test 1", byteArrayOf(11), "12")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }



    private class AdderOrEnderForTest(
            val delegate: WhereBuilderImpl<ModelForTest, AdderOrEnderForTest>) :
            AdderOrEnder<ModelForTest, AdderOrEnderForTest> {

        fun build() = delegate.build()

        override fun and(): AfterSimpleConnectorAdder<ModelForTest, AdderOrEnderForTest> {
            delegate.and()
            return delegate
        }

        override fun or(): AfterSimpleConnectorAdder<ModelForTest, AdderOrEnderForTest> {
            delegate.or()
            return delegate
        }
    }



    companion object {

        private fun assertWhereEquality(actualWhere: Where, expectedWhere: Where) {
            assertThat(actualWhere.buildString()).isEqualTo(expectedWhere.buildString())
        }


        private fun Where.buildString(): String {
            val clauseWithPlaceholders = buildWhereClauseWithPlaceholders(whereSections)
            val argsString = arguments.joinToString() {
                when (it) {
                    is ByteArray -> String(it)
                    else -> it.toString()
                }
            }

            return "Where clause: $clauseWithPlaceholders; args: $argsString"
        }
    }
}