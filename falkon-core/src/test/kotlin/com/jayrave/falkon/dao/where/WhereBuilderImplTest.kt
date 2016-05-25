package com.jayrave.falkon.dao.where

import com.jayrave.falkon.*
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink
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
        val expectedWhere = Where("", emptyList())
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testEq() {
        val actualWhere = builder.eq(table.int, 5).build()
        val expectedWhere = Where("int = ?", listOf(5))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testNotEq() {
        val actualWhere = builder.notEq(table.int, 5).build()
        val expectedWhere = Where("int != ?", listOf(5))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testGt() {
        val actualWhere = builder.gt(table.int, 5).build()
        val expectedWhere = Where("int > ?", listOf(5))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testGe() {
        val actualWhere = builder.ge(table.int, 5).build()
        val expectedWhere = Where("int >= ?", listOf(5))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testLt() {
        val actualWhere = builder.lt(table.int, 5).build()
        val expectedWhere = Where("int < ?", listOf(5))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testLe() {
        val actualWhere = builder.le(table.int, 5).build()
        val expectedWhere = Where("int <= ?", listOf(5))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testBetween() {
        val actualWhere = builder.between(table.int, 5, 8).build()
        val expectedWhere = Where("int BETWEEN ? AND ?", listOf(5, 8))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testLike() {
        val actualWhere = builder.like(table.int, "5").build()
        val expectedWhere = Where("int LIKE ?", listOf("5"))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testIsNull() {
        val actualWhere = builder.isNull(table.int).build()
        val expectedWhere = Where("int IS NULL", emptyList())
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testIsNotNull() {
        val actualWhere = builder.isNotNull(table.int).build()
        val expectedWhere = Where("int IS NOT NULL", emptyList())
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testSimpleAnd() {
        val actualWhere = builder.eq(table.int, 5).and().eq(table.string, "test").build()
        val expectedWhere = Where("int = ? AND string = ?", listOf(5, "test"))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testSimpleOr() {
        val actualWhere = builder.eq(table.int, 5).or().eq(table.string, "test").build()
        val expectedWhere = Where("int = ? OR string = ?", listOf(5, "test"))
        assertThat(actualWhere).isEqualTo(expectedWhere)
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

        val expectedWhere = Where("(int = ? AND string = ?)", listOf(5, "test"))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testCompoundOr() {
        val actualWhere = builder
                .or {
                    eq(table.int, 5)
                    eq(table.string, "test")
                }.build()

        val expectedWhere = Where("(int = ? OR string = ?)", listOf(5, "test"))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleEqPredicates() {
        val actualWhere = builder
                .eq(table.int, 5).and()
                .eq(table.string, "test 1").or()
                .eq(table.nullableInt, 6).build()

        val expectedWhere = Where("int = ? AND string = ? OR nullable_int = ?", listOf(5, "test 1", 6))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleNotEqPredicates() {
        val actualWhere = builder
                .notEq(table.int, 5).and()
                .notEq(table.string, "test 1").or()
                .notEq(table.nullableInt, 6).build()

        val expectedWhere = Where("int != ? AND string != ? OR nullable_int != ?", listOf(5, "test 1", 6))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleGtPredicates() {
        val actualWhere = builder
                .gt(table.int, 5).and()
                .gt(table.string, "test 1").or()
                .gt(table.nullableInt, 6).build()

        val expectedWhere = Where("int > ? AND string > ? OR nullable_int > ?", listOf(5, "test 1", 6))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleGePredicates() {
        val actualWhere = builder
                .ge(table.int, 5).and()
                .ge(table.string, "test 1").or()
                .ge(table.nullableInt, 6).build()

        val expectedWhere = Where("int >= ? AND string >= ? OR nullable_int >= ?", listOf(5, "test 1", 6))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleLtPredicates() {
        val actualWhere = builder
                .lt(table.int, 5).and()
                .lt(table.string, "test 1").or()
                .lt(table.nullableInt, 6).build()

        val expectedWhere = Where("int < ? AND string < ? OR nullable_int < ?", listOf(5, "test 1", 6))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleLePredicates() {
        val actualWhere = builder
                .le(table.int, 5).and()
                .le(table.string, "test 1").or()
                .le(table.nullableInt, 6).build()

        val expectedWhere = Where("int <= ? AND string <= ? OR nullable_int <= ?", listOf(5, "test 1", 6))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleBetweenPredicates() {
        val actualWhere = builder
                .between(table.int, 5, 6).and()
                .between(table.string, "test 1", "test 2").or()
                .between(table.nullableInt, 7, 8).build()

        val expectedWhere = Where(
                "int BETWEEN ? AND ? AND string BETWEEN ? AND ? OR nullable_int BETWEEN ? AND ?",
                listOf(5, 6, "test 1", "test 2", 7, 8)
        )

        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleLikePredicates() {
        val actualWhere = builder
                .like(table.int, "5").and()
                .like(table.string, "test 1").or()
                .like(table.nullableInt, "6").build()

        val expectedWhere = Where("int LIKE ? AND string LIKE ? OR nullable_int LIKE ?", listOf("5", "test 1", "6"))
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleIsNullPredicates() {
        val actualWhere = builder
                .isNull(table.int).and()
                .isNull(table.string).or()
                .isNull(table.nullableInt).build()

        val expectedWhere = Where("int IS NULL AND string IS NULL OR nullable_int IS NULL", emptyList())
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMultipleNotNullPredicates() {
        val actualWhere = builder
                .isNotNull(table.int).and()
                .isNotNull(table.string).or()
                .isNotNull(table.nullableInt).build()

        val expectedWhere = Where("int IS NOT NULL AND string IS NOT NULL OR nullable_int IS NOT NULL", emptyList())
        assertThat(actualWhere).isEqualTo(expectedWhere)
    }


    @Test
    fun testMixedPredicates() {
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
                    ge(table.nullableString, "test 4")
                    like(table.string, "test 5")
                }.and()
                .isNull(table.nullableInt).or()
                .isNotNull(table.int).build()

        val expectedWhere = Where(
                "int = ? OR nullable_string != ? AND string > ? OR nullable_int <= ? AND " +
                        "(int BETWEEN ? AND ? OR string >= ?) OR (nullable_string >= ? AND string LIKE ?) AND " +
                        "nullable_int IS NULL OR int IS NOT NULL",

                listOf(5, "test 1", "test 2", 7, 8, 9, "test 3", "test 4", "test 5")
        )

        assertThat(actualWhere).isEqualTo(expectedWhere)
    }



    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test",
            val nullableInt: Int? = null,
            val nullableString: String? = null
    )



    private class TableForTest(
            configuration: TableConfiguration<Engine<Sink>, Sink> = defaultConfiguration()) :
            BaseTable<ModelForTest, Int, Engine<Sink>, Sink>("test", configuration) {

        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

        val int = col(ModelForTest::int)
        val string = col(ModelForTest::string)
        val nullableInt = col(ModelForTest::nullableInt)
        val nullableString = col(ModelForTest::nullableString)

        companion object {
            private fun defaultConfiguration(): TableConfiguration<Engine<Sink>, Sink> {
                val configuration = TableConfigurationImpl<Engine<Sink>, Sink>(mock())
                configuration.registerDefaultConverters()
                return configuration
            }
        }
    }



    private class AdderOrEnderForTest(val delegate: WhereBuilderImpl<ModelForTest, AdderOrEnderForTest>) :
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
}