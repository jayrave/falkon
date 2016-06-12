package com.jayrave.falkon.dao.where

import com.jayrave.falkon.*
import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.dao.testLib.buildWhereClauseWithPlaceholders
import com.jayrave.falkon.engine.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.*
import com.jayrave.falkon.exceptions.SQLSyntaxErrorException
import com.nhaarman.mockito_kotlin.mock
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
        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testEq() {
        val actualWhere = builder.eq(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "int")), listOf(5)
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testNotEq() {
        val actualWhere = builder.notEq(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "int")), listOf(5)
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testGt() {
        val actualWhere = builder.gt(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "int")), listOf(5)
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testGe() {
        val actualWhere = builder.ge(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, "int")), listOf(5)
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testLt() {
        val actualWhere = builder.lt(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "int")), listOf(5)
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testLe() {
        val actualWhere = builder.le(table.int, 5).build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "int")), listOf(5)
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testBetween() {
        val actualWhere = builder.between(table.int, 5, 8).build()
        val expectedWhere = Where(listOf(BetweenPredicate("int")), listOf(5, 8))
        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testLike() {
        val actualWhere = builder.like(table.int, "5").build()
        val expectedWhere = Where(
                listOf(OneArgPredicate(OneArgPredicate.Type.LIKE, "int")), listOf("5")
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNull() {
        val actualWhere = builder.isNull(table.int).build()
        val expectedWhere = Where(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NULL, "int")), emptyList()
        )

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testIsNotNull() {
        val actualWhere = builder.isNotNull(table.int).build()
        val expectedWhere = Where(
                listOf(NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "int")), emptyList()
        )

        assertEquals(actualWhere, expectedWhere)
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

        assertEquals(actualWhere, expectedWhere)
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

        assertEquals(actualWhere, expectedWhere)
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

        assertEquals(actualWhere, expectedWhere)
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

        assertEquals(actualWhere, expectedWhere)
    }


    @Test
    fun testWhereWithAllSections() {
        val actualWhere = builder
                .eq(table.int, 5).or()
                .notEq(table.nullableString, "test 1").and()
                .gt(table.string, "test 2").or()
                .le(table.nullableInt, 7).and()
                .or {
                    between(table.int, 8, 9)
                    ge(table.string, "test 3")
                }.or()
                .and {
                    lt(table.string, "test 4")
                    like(table.nullableInt, "test 5")
                }.and()
                .isNull(table.nullableInt).or()
                .isNotNull(table.int).build()

        val expectedWhere = Where(
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "int"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "nullable_string"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "string"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "nullable_int"),
                        SimpleConnector(SimpleConnector.Type.AND),
                        CompoundConnector(
                                CompoundConnector.Type.OR,
                                listOf(
                                        BetweenPredicate("int"),
                                        OneArgPredicate(
                                                OneArgPredicate.Type.GREATER_THAN_OR_EQ, "string"
                                        )
                                )
                        ), SimpleConnector(SimpleConnector.Type.OR),
                        CompoundConnector(
                                CompoundConnector.Type.AND,
                                listOf(
                                        OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "string"),
                                        OneArgPredicate(OneArgPredicate.Type.LIKE, "nullable_int")
                                )
                        ), SimpleConnector(SimpleConnector.Type.AND),
                        NoArgPredicate(NoArgPredicate.Type.IS_NULL, "nullable_int"),
                        SimpleConnector(SimpleConnector.Type.OR),
                        NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "int")
                ), listOf(5, "test 1", "test 2", 7, 8, 9, "test 3", "test 4", "test 5")
        )

        assertEquals(actualWhere, expectedWhere)
    }



    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test",
            val nullableInt: Int? = null,
            val nullableString: String? = null
    )



    private class TableForTest(
            configuration: TableConfiguration = defaultConfiguration()) :
            BaseTable<ModelForTest, Int, Dao<ModelForTest, Int>>("test", configuration) {

        override val dao: Dao<ModelForTest, Int> = mock()
        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

        val int = col(ModelForTest::int)
        val string = col(ModelForTest::string)
        val nullableInt = col(ModelForTest::nullableInt)
        val nullableString = col(ModelForTest::nullableString)

        companion object {
            private fun defaultConfiguration(): TableConfiguration {
                val configuration = TableConfigurationImpl(mock())
                configuration.registerDefaultConverters()
                return configuration
            }
        }
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

        private fun assertEquals(actualWhere: Where, expectedWhere: Where) {
            assertThat(actualWhere.buildString()).isEqualTo(expectedWhere.buildString())
        }


        private fun Where.buildString(): String {
            val clauseWithPlaceholders = buildWhereClauseWithPlaceholders(whereSections)
            val argsString = arguments.joinToString()

            return "Where clause: $clauseWithPlaceholders; args: $argsString"
        }
    }
}