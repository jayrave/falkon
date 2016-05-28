package com.jayrave.falkon.dao.query

import com.jayrave.falkon.*
import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Sink
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class QueryBuilderImplTest {

    private val table = TableForTest()
    private val engine = table.configuration.engine
    private val builder = QueryBuilderImpl(table)

    @Test
    fun testQueryingWithoutSettingAnything() {
        builder.query()
        verifyCallToQuery(
                distinct = false, columns = null, whereClause = null, whereArgs = null,
                groupBy = null, having = null, orderBy = null, limit = null, offset = null
        )
    }


    @Test
    fun testWithDistinct() {
        builder.distinct().query()
        verifyCallToQuery(distinct = true)
    }


    @Test
    fun testWithSingleSelectedColumn() {
        builder.select(table.int).query()
        verifyCallToQuery(columns = listOf("int"))
    }


    @Test
    fun testWithMultipleSelectedColumns() {
        builder.select(table.int, table.nullableString).query()
        verifyCallToQuery(columns = listOf("int", "nullable_string"))
    }


    @Test
    fun testRedefiningSelectedColumnsOverwritesExisting() {
        builder.select(table.int).select(table.string).query()
        verifyCallToQuery(columns = listOf("string"))
    }


    @Test
    fun testWithWhere() {
        builder.where().eq(table.int, 5).query()
        verifyCallToQuery(whereClause = "int = ?", whereArgs = listOf(5))
    }


    @Test
    fun testGroupByWithOneColumn() {
        builder.groupBy(table.int).query()
        verifyCallToQuery(groupBy = listOf("int"))
    }


    @Test
    fun testGroupByWithMultipleColumns() {
        builder.groupBy(table.string, table.nullableInt).query()
        verifyCallToQuery(groupBy = listOf("string", "nullable_int"))
    }


    @Test
    fun testRedefiningGroupByOverwritesExisting() {
        builder.groupBy(table.int).groupBy(table.string).query()
        verifyCallToQuery(groupBy = listOf("string"))
    }


    @Test
    fun testOrderByWithOneColumn() {
        builder.orderBy(table.int, true).query()
        verifyCallToQuery(orderBy = listOf(Pair("int", true)))
    }


    @Test
    fun testOrderByWithMultipleColumns() {
        builder.orderBy(table.int, true).orderBy(table.nullableInt, false).query()
        verifyCallToQuery(orderBy = listOf(Pair("int", true), Pair("nullable_int", false)))
    }


    @Test
    fun testSubsequentOrderByForTheSameColumnIsNoOp() {
        builder.orderBy(table.int, true).orderBy(table.int, false).query()
        verifyCallToQuery(orderBy = listOf(Pair("int", true)))
    }


    @Test
    fun testLimit() {
        builder.limit(50).query()
        verifyCallToQuery(limit = 50)
    }


    @Test
    fun testOffset() {
        builder.offset(72).query()
        verifyCallToQuery(offset = 72)
    }


    @Test
    fun testComplexQueryWithWhereAtLast() {
        builder
                .distinct()
                .select(table.string)
                .groupBy(table.nullableInt)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)
                .where().eq(table.nullableString, "test")
                .query()

        verifyComplexWhere()
    }


    @Test
    fun testComplexQueryWithWhereAtFirst() {
        builder
                .where().eq(table.nullableString, "test")
                .distinct()
                .select(table.string)
                .groupBy(table.nullableInt)
                .orderBy(table.int, true)
                .limit(5)
                .offset(8)
                .query()

        verifyComplexWhere()
    }


    @Test
    fun testComplexQueryWithCrazyOrdering() {
        builder
                .limit(5)
                .orderBy(table.int, true)
                .where().eq(table.nullableString, "test")
                .offset(8)
                .groupBy(table.nullableInt)
                .select(table.string)
                .distinct()
                .query()

        verifyComplexWhere()
    }


    private fun verifyComplexWhere() {
        verifyCallToQuery(
                distinct = true, columns = listOf("string"), whereClause = "nullable_string = ?",
                whereArgs = listOf("test"), groupBy = listOf("nullable_int"), orderBy = listOf(Pair("int", true)),
                limit = 5, offset = 8
        )
    }


    private fun verifyCallToQuery(
            distinct: Boolean = false, columns: Iterable<String>? = null, whereClause: String? = null,
            whereArgs: Iterable<Any?>? = null, groupBy: Iterable<String>? = null, having: String? = null,
            orderBy: Iterable<Pair<String, Boolean>>? = null, limit: Long? = null, offset: Long? = null) {

        verify(engine).query(
                eq(table.name), eq(distinct),
                if (columns != null) eq(columns) else isNull<Iterable<String>>(),
                if (whereClause != null) eq(whereClause) else isNull<String>(),
                if (whereArgs != null) eq(whereArgs) else isNull<Iterable<*>>(),
                if (groupBy != null) eq(groupBy) else isNull<Iterable<String>>(),
                if (having != null) eq(having) else isNull<String>(),
                if (orderBy != null) eq(orderBy) else isNull<Iterable<Pair<String, Boolean>>>(),
                if (limit != null) eq(limit) else isNull<Long>(),
                if (offset != null) eq(offset) else isNull<Long>()
        )
    }



    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test",
            val nullableInt: Int? = null,
            val nullableString: String? = null
    )



    private class TableForTest(
            configuration: TableConfiguration<Sink> = defaultConfiguration()) :
            BaseTable<ModelForTest, Int, Dao<ModelForTest, Int, Sink>, Sink>("test", configuration) {

        override val dao: Dao<ModelForTest, Int, Sink> = mock()
        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

        val int = col(ModelForTest::int)
        val string = col(ModelForTest::string)
        val nullableInt = col(ModelForTest::nullableInt)
        val nullableString = col(ModelForTest::nullableString)

        companion object {
            private fun defaultConfiguration(): TableConfiguration<Sink> {
                val configuration = TableConfigurationImpl<Sink>(mock())
                configuration.registerDefaultConverters()
                return configuration
            }
        }
    }
}