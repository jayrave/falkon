package com.jayrave.falkon.dao.where.lenient

import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.query.QueryImpl
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.assertWhereEquality
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereImpl
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.*
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class WhereBuilderImplTest {

    @Test
    fun testEmptyWhere() {
        val builder = newBuilder()
        val actualWhere = builder.build()
        val expectedWhere = WhereImpl(emptyList(), emptyList())
        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testEq() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.eq(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name)), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testNotEq() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.notEq(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table.int.name)), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testGt() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.gt(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.int.name)),
                listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testGe() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.ge(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, table.int.name)),
                listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLt() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.lt(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table.int.name)), listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLe() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.le(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, table.int.name)),
                listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testBetween() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.between(table.int, 5, 8).build()
        val expectedWhere = WhereImpl(listOf(BetweenPredicate(table.int.name)), listOf(5, 8))
        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testLike() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.like(table.int, "5").build()
        val expectedWhere = WhereImpl(
                listOf(OneArgPredicate(OneArgPredicate.Type.LIKE, table.int.name)), listOf("5")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsInWithSubQueryWithNoArgs() {
        val table = TableForTest()
        val builder = newBuilder()
        val subQuery = QueryImpl(emptyList(), "test sub query", emptyList())
        val actualWhere = builder.isIn(table.int, subQuery).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicateWithSubQuery(
                        MultiArgPredicateWithSubQuery.Type.IS_IN,
                        table.int.name, subQuery.sql, 0
                )), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsInWithSubQueryWithMultipleArgs() {
        val table = TableForTest()
        val builder = newBuilder()
        val subQuery = QueryImpl(emptyList(), "test sub query", listOf(5, "test 6"))
        val actualWhere = builder.isIn(table.int, subQuery).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicateWithSubQuery(
                        MultiArgPredicateWithSubQuery.Type.IS_IN,
                        table.int.name, subQuery.sql, 2
                )), listOf(5, "test 6")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsInWithSingleArg() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.isIn(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_IN, table.int.name, 1)),
                listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsInWithMultipleArgs() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.isIn(table.int, 5, 6, 7, 8).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_IN, table.int.name, 4)),
                listOf(5, 6, 7, 8)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotInWithSubQueryWithNoArgs() {
        val table = TableForTest()
        val builder = newBuilder()
        val subQuery = QueryImpl(emptyList(), "test sub query", emptyList())
        val actualWhere = builder.isNotIn(table.int, subQuery).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicateWithSubQuery(
                        MultiArgPredicateWithSubQuery.Type.IS_NOT_IN,
                        table.int.name, subQuery.sql, 0
                )), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotInWithSubQueryWithMultipleArgs() {
        val table = TableForTest()
        val builder = newBuilder()
        val subQuery = QueryImpl(emptyList(), "test sub query", listOf(5, "test 6"))
        val actualWhere = builder.isNotIn(table.int, subQuery).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicateWithSubQuery(
                        MultiArgPredicateWithSubQuery.Type.IS_NOT_IN,
                        table.int.name, subQuery.sql, 2
                )), listOf(5, "test 6")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotInWithSingleArg() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.isNotIn(table.int, 5).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, table.int.name, 1)),
                listOf(5)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotInWithMultipleArgs() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.isNotIn(table.int, 5, 6, 7, 8).build()
        val expectedWhere = WhereImpl(
                listOf(MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, table.int.name, 4)),
                listOf(5, 6, 7, 8)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNull() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.isNull(table.int).build()
        val expectedWhere = WhereImpl(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NULL, table.int.name)), emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotNull() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.isNotNull(table.int).build()
        val expectedWhere = WhereImpl(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, table.int.name)),
                emptyList()
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testSimpleAnd() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.eq(table.int, 5).and().eq(table.string, "test").build()
        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name)
                ), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testSimpleOr() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder.eq(table.int, 5).or().eq(table.string, "test").build()
        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name)
                ), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testEmptyCompoundAndThrows() {
        val builder = newBuilder()
        builder.and {}.build()
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testEmptyCompoundOrThrows() {
        val builder = newBuilder()
        builder.or {}.build()
    }


    @Test
    fun testCompoundAnd() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder
                .and {
                    eq(table.int, 5)
                    eq(table.string, "test")
                }.build()

        val expectedWhere = WhereImpl(
                listOf(CompoundConnector(
                        CompoundConnector.Type.AND,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                                OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name)
                        )
                )), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testCompoundOr() {
        val table = TableForTest()
        val builder = newBuilder()
        val actualWhere = builder
                .or {
                    eq(table.int, 5)
                    eq(table.string, "test")
                }.build()

        val expectedWhere = WhereImpl(
                listOf(CompoundConnector(
                        CompoundConnector.Type.OR,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                                OneArgPredicate(OneArgPredicate.Type.EQ, table.string.name)
                        )
                )), listOf(5, "test")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testMultiLevelCompoundAnd() {
        val table = TableForTest()
        val builder = newBuilder()
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
                                OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.string.name),
                                CompoundConnector(
                                        CompoundConnector.Type.AND,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, table.short.name),
                                                NoArgPredicate(NoArgPredicate.Type.IS_NULL, table.nullableInt.name),
                                                CompoundConnector(
                                                        CompoundConnector.Type.OR,
                                                        listOf(
                                                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, table.double.name),
                                                                NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, table.nullableInt.name)
                                                        )
                                                )
                                        )
                                ),
                                CompoundConnector(
                                        CompoundConnector.Type.OR,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table.blob.name),
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table.long.name)
                                        )
                                )
                        )
                )), listOf(5, "test", 6, 7.0, byteArrayOf(8), 9)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testMultiLevelCompoundOr() {
        val table = TableForTest()
        val builder = newBuilder()
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
                                OneArgPredicate(OneArgPredicate.Type.EQ, table.int.name),
                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.string.name),
                                CompoundConnector(
                                        CompoundConnector.Type.OR,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, table.short.name),
                                                NoArgPredicate(NoArgPredicate.Type.IS_NULL, table.nullableInt.name),
                                                CompoundConnector(
                                                        CompoundConnector.Type.AND,
                                                        listOf(
                                                                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, table.double.name),
                                                                NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, table.nullableInt.name)
                                                        )
                                                )
                                        )
                                ),
                                CompoundConnector(
                                        CompoundConnector.Type.AND,
                                        listOf(
                                                OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table.blob.name),
                                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table.long.name)
                                        )
                                )
                        )
                )), listOf(5, "test", 6, 7.0, byteArrayOf(8), 9)
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testWhereWithAllSections() {
        val table = TableForTest()
        val builder = newBuilder()
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
                .isIn(
                        table.nullableString,
                        QueryImpl(
                                listOf("table_1"), "test sub query for is in",
                                listOf("test 13", "test 14")
                        )
                ).or()
                .isIn(table.nullableFloat, 15F, 16F, 17F).or()
                .isNotIn(
                        table.blob,
                        QueryImpl(
                                listOf("table_1"), "test sub query for is not in",
                                listOf(byteArrayOf(18), 19.0)
                        )
                ).or()
                .isNotIn(table.nullableDouble, 20.0).and()
                .isNull(table.nullableInt).or()
                .isNotNull(table.int).build()

        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.short.name),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table.int.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.name),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, table.float.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        CompoundConnector(
                                CompoundConnector.Type.OR,
                                listOf(
                                        BetweenPredicate(table.double.name),
                                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, table.string.name)
                                )
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        CompoundConnector(
                                CompoundConnector.Type.AND,
                                listOf(
                                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table.blob.name),
                                        OneArgPredicate(OneArgPredicate.Type.LIKE, table.long.name)
                                )
                        ), SimpleConnector(SimpleConnector.Type.AND),
                        MultiArgPredicateWithSubQuery(
                                MultiArgPredicateWithSubQuery.Type.IS_IN, table.nullableString.name,
                                "test sub query for is in", 2
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_IN, table.nullableFloat.name, 3),
                        SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicateWithSubQuery(
                                MultiArgPredicateWithSubQuery.Type.IS_NOT_IN, table.blob.name,
                                "test sub query for is not in", 2
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, table.nullableDouble.name, 1),
                        SimpleConnector(SimpleConnector.Type.AND),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, table.nullableInt.name),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, table.int.name)
                ),
                listOf(
                        5, 6, 7, 8f, 9.0, 10.0, "test 1", byteArrayOf(11), "12", "test 13",
                        "test 14", 15F, 16F, 17F, byteArrayOf(18), 19.0, 20.0
                )
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testWhereWithAllSectionsWithQualifiedColumnName() {
        val table = TableForTest()
        val builder = newBuilder(qualifyColumnNames = true)
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
                .isIn(
                        table.nullableString,
                        QueryImpl(
                                listOf("table_1"), "test sub query for is in",
                                listOf("test 13", "test 14")
                        )
                ).or()
                .isIn(table.nullableFloat, 15F, 16F, 17F).or()
                .isNotIn(
                        table.blob,
                        QueryImpl(
                                listOf("table_1"), "test sub query for is not in",
                                listOf(byteArrayOf(18), 19.0)
                        )
                ).or()
                .isNotIn(table.nullableDouble, 20.0).and()
                .isNull(table.nullableInt).or()
                .isNotNull(table.int).build()

        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table.short.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table.int.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, table.float.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        CompoundConnector(
                                CompoundConnector.Type.OR,
                                listOf(
                                        BetweenPredicate(table.double.qualifiedName),
                                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, table.string.qualifiedName)
                                )
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        CompoundConnector(
                                CompoundConnector.Type.AND,
                                listOf(
                                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table.blob.qualifiedName),
                                        OneArgPredicate(OneArgPredicate.Type.LIKE, table.long.qualifiedName)
                                )
                        ), SimpleConnector(SimpleConnector.Type.AND),
                        MultiArgPredicateWithSubQuery(
                                MultiArgPredicateWithSubQuery.Type.IS_IN, table.nullableString.qualifiedName,
                                "test sub query for is in", 2
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_IN, table.nullableFloat.qualifiedName, 3),
                        SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicateWithSubQuery(
                                MultiArgPredicateWithSubQuery.Type.IS_NOT_IN, table.blob.qualifiedName,
                                "test sub query for is not in", 2
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        MultiArgPredicate(MultiArgPredicate.Type.IS_NOT_IN, table.nullableDouble.qualifiedName, 1),
                        SimpleConnector(SimpleConnector.Type.AND),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, table.nullableInt.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, table.int.qualifiedName)
                ),
                listOf(
                        5, 6, 7, 8f, 9.0, 10.0, "test 1", byteArrayOf(11), "12", "test 13",
                        "test 14", 15F, 16F, 17F, byteArrayOf(18), 19.0, 20.0
                )
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testWhereInvolvingColumnsFromMultipleTables() {
        val table1 = TableForTest("table_name_1")
        val table2 = TableForTest("table_name_2")
        val table3 = TableForTest("table_name_3")
        val builder = newBuilder(qualifyColumnNames = true)
        val actualWhere = builder
                .eq(table1.short, 5).or()
                .notEq(table2.int, 6).and()
                .or {
                    between(table3.double, 7.0, 8.0)
                    ge(table1.string, "test 9")
                }.or()
                .and {
                    lt(table2.blob, byteArrayOf(10))
                    like(table3.long, "11")
                }.build()

        val expectedWhere = WhereImpl(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, table1.short.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, table2.int.qualifiedName),
                        SimpleConnector(SimpleConnector.Type.AND),
                        CompoundConnector(
                                CompoundConnector.Type.OR,
                                listOf(
                                        BetweenPredicate(table3.double.qualifiedName),
                                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, table1.string.qualifiedName)
                                )
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        CompoundConnector(
                                CompoundConnector.Type.AND,
                                listOf(
                                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN, table2.blob.qualifiedName),
                                        OneArgPredicate(OneArgPredicate.Type.LIKE, table3.long.qualifiedName)
                                )
                        )
                ),
                listOf(5, 6, 7.0, 8.0, "test 9", byteArrayOf(10), "11")
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }


    @Test
    fun testNullHandling() {
        val table = TableForTest()
        val builder = newBuilder()
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
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableShort.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableInt.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableLong.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableFloat.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableDouble.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableString.name),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, table.nullableBlob.name)
                ),

                listOf(
                        TypedNull(Type.SHORT), TypedNull(Type.INT), TypedNull(Type.LONG),
                        TypedNull(Type.FLOAT), TypedNull(Type.DOUBLE), TypedNull(Type.STRING),
                        TypedNull(Type.BLOB)
                )
        )

        assertWhereEquality(actualWhere, expectedWhere)
    }



    private interface AdderOrEnderForTest : AdderOrEnder<AdderOrEnderForTest> {
        fun build(): Where
    }



    companion object {

        private fun newBuilder(qualifyColumnNames: Boolean = false):
                WhereBuilderImpl<AdderOrEnderForTest> {

            var builder: WhereBuilderImpl<AdderOrEnderForTest>? = null
            builder = WhereBuilderImpl<AdderOrEnderForTest>(qualifyColumnNames) {
                val impl = AdderOrEnderForTestImpl(it)
                impl.builder = builder!!
                impl
            }

            return builder
        }


        private class AdderOrEnderForTestImpl(
                private val delegate: AdderOrEnder<AdderOrEnderForTest>) :
                AdderOrEnderForTest {

            lateinit var builder: WhereBuilderImpl<*>
            override fun build() = builder.build()
            override fun and() = delegate.and()
            override fun or() = delegate.or()
        }
    }
}