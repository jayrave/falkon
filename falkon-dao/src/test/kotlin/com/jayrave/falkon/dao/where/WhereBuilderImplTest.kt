package com.jayrave.falkon.dao.where

import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class WhereBuilderImplTest {

    private val table = TableForTest()
    private val builder: WhereBuilderImpl<ModelForTest, AdderOrEnderForTest> = WhereBuilderImpl {
        AdderOrEnderForTest(it)
    }


    @Test
    fun testEmptyWhere() {
        val actualWhere = builder.build()
        val expectedWhere = WhereImpl(emptyList(), emptyList())
        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testEq() {
        val actualWhere = builder.eq(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testNotEq() {
        val actualWhere = builder.notEq(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testGt() {
        val actualWhere = builder.gt(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testGe() {
        val actualWhere = builder.ge(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLt() {
        val actualWhere = builder.lt(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLe() {
        val actualWhere = builder.le(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "int")), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testBetween() {
        val actualWhere = builder.between(table.int, 5, 8).build()
        val expectedWhere = WhereImpl(listOf(BetweenPredicate("int")), listOf(5, 8))
        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLike() {
        val actualWhere = builder.like(table.int, "5").build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.LIKE, "int")), listOf("5")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsInWithSingleArg() {
        val actualWhere = builder.isIn(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_IN, "int", 1)), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsInWithMultipleArgs() {
        val actualWhere = builder.isIn(table.int, 5, 6, 7, 8).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_IN, "int", 4)),
                listOf(5, 6, 7, 8)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotInWithSingleArg() {
        val actualWhere = builder.isNotIn(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, "int", 1)), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotInWithMultipleArgs() {
        val actualWhere = builder.isNotIn(table.int, 5, 6, 7, 8).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, "int", 4)),
                listOf(5, 6, 7, 8)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNull() {
        val actualWhere = builder.isNull(table.int).build()
        val expectedWhere = WhereImpl(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NULL, "int")), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotNull() {
        val actualWhere = builder.isNotNull(table.int).build()
        val expectedWhere = WhereImpl(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "int")), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testSimpleAnd() {
        val actualWhere = builder.eq(table.int, 5).and().eq(table.string, "test").build()
        val expectedWhere = WhereImpl(
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
        val expectedWhere = WhereImpl(
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

        val expectedWhere = WhereImpl(
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

        val expectedWhere = WhereImpl(
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
    fun testMultiLevelCompoundAnd() {
        val actualWhere = builder
                .and {
                    eq(table.int, 5)
                    gt(table.string, "test")
                    and {
                        le(table.short, 6)
                        isNull(table.nullableInt)
                        or {
                            ge(table.double, 7.0)
                            isNotNull(table.nullableInt)
                        }
                    }
                    or {
                        notEq(table.blob, byteArrayOf(8))
                        lt(table.long, 9)
                    }
                }.build()

        val expectedWhere = WhereImpl(
                listOf(CompoundConnector(
                        CompoundConnector.Type.AND,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "string"),
                                CompoundConnector(
                                        CompoundConnector.Type.AND,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "short"),
                                                NoArgPredicate(NoArgPredicate.Type.IS_NULL, "nullable_int"),
                                                CompoundConnector(
                                                        CompoundConnector.Type.OR,
                                                        listOf(
                                                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, "double"),
                                                                NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "nullable_int")
                                                        )
                                                )
                                        )
                                ),
                                CompoundConnector(
                                        CompoundConnector.Type.OR,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "blob"),
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "long")
                                        )
                                )
                        )
                )), listOf(5, "test", 6, 7.0, byteArrayOf(8), 9)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testMultiLevelCompoundOr() {
        val actualWhere = builder
                .or {
                    eq(table.int, 5)
                    gt(table.string, "test")
                    or {
                        le(table.short, 6)
                        isNull(table.nullableInt)
                        and {
                            ge(table.double, 7.0)
                            isNotNull(table.nullableInt)
                        }
                    }
                    and {
                        notEq(table.blob, byteArrayOf(8))
                        lt(table.long, 9)
                    }
                }.build()

        val expectedWhere = WhereImpl(
                listOf(CompoundConnector(
                        CompoundConnector.Type.OR,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "string"),
                                CompoundConnector(
                                        CompoundConnector.Type.OR,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "short"),
                                                NoArgPredicate(NoArgPredicate.Type.IS_NULL, "nullable_int"),
                                                CompoundConnector(
                                                        CompoundConnector.Type.AND,
                                                        listOf(
                                                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, "double"),
                                                                NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "nullable_int")
                                                        )
                                                )
                                        )
                                ),
                                CompoundConnector(
                                        CompoundConnector.Type.AND,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "blob"),
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "long")
                                        )
                                )
                        )
                )), listOf(5, "test", 6, 7.0, byteArrayOf(8), 9)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testWhereWithAllSections() {
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
                .isNotNull(table.int).build()

        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "short"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_int"),
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
                        MultiArgPredicate(MultiArgPredicate.Type.IS_IN, "nullable_float", 3),
                        SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, "nullable_double", 1),
                        SimpleConnector(SimpleConnector.Type.AND),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, "nullable_int"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "int")
                ),
                listOf(
                        5, 6, 7, 8f, 9.0, 10.0, "test 1", byteArrayOf(11), "12",
                        13F, 14F, 15F, 16.0
                )
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testNullHandling() {
        val actualWhere = builder
                .gt(table.nullableShort, null).and()
                .gt(table.nullableInt, null).and()
                .gt(table.nullableLong, null).and()
                .gt(table.nullableFloat, null).and()
                .gt(table.nullableDouble, null).and()
                .gt(table.nullableString, null).and()
                .gt(table.nullableBlob, null).
                build()

        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_short"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_long"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_float"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_double"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_string"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "nullable_blob")
                ),

                listOf(
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB)
                )
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