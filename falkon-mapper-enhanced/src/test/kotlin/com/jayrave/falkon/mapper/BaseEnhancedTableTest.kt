package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypeTranslator
import com.jayrave.falkon.sqlBuilders.h2.H2CreateTableSqlBuilder
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BaseEnhancedTableTest {

    @Test
    fun `create table without primary key`() {
        val table = object : BaseEnhancedTableForTest() {
            val int = col(ModelForTest::int)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (${table.int.name} DUMMY_TYPE)"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with simple primary key`() {
        val table = object : BaseEnhancedTableForTest() {
            val id = col(ModelForTest::int, isId = true)
            val nullableInt = col(ModelForTest::nullableInt)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.id.name} DUMMY_TYPE, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "PRIMARY KEY (${table.id.name}))"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with composite primary key`() {
        val table = object : BaseEnhancedTableForTest() {
            val id1 = col(ModelForTest::int, isId = true)
            val id2 = col(ModelForTest::nullableString, isId = true)
            val nullableInt = col(ModelForTest::nullableInt)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.id1.name} DUMMY_TYPE, " +
                "${table.id2.name} DUMMY_TYPE, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "PRIMARY KEY (${table.id1.name}, ${table.id2.name}))"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with foreign column`() {
        val foreignTable = ForeignTableForTest()
        val table = object : BaseEnhancedTableForTest() {
            val int = foreignCol(ModelForTest::int, foreignColumn = foreignTable.col)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.int.name} DUMMY_TYPE, " +
                "FOREIGN KEY (${table.int.name}) REFERENCES ${foreignTable.name}(${foreignTable.col.name}))"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with column having max size`() {
        val table = object : BaseEnhancedTableForTest() {
            val int = col(ModelForTest::int, maxSize = 24)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (${table.int.name} DUMMY_TYPE(24))"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with non null column`() {
        val table = object : BaseEnhancedTableForTest() {
            val string = col(ModelForTest::string, isNonNull = true)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.string.name} DUMMY_TYPE NOT NULL)"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with auto incrementing column`() {
        val table = object : BaseEnhancedTableForTest() {
            val string = col(ModelForTest::string, autoIncrement = true)
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.string.name} DUMMY_TYPE AUTO_INCREMENT)"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with uniqueness constraints`() {
        val table = object : BaseEnhancedTableForTest() {
            val int = col(ModelForTest::int, isUnique = true)
            val nullableInt = col(ModelForTest::nullableInt)
            val nullableString = col(ModelForTest::nullableString)

            init {
                addUniquenessConstraint(nullableInt, nullableString)
            }
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.int.name} DUMMY_TYPE, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "${table.nullableString.name} DUMMY_TYPE, " +
                "UNIQUE (${table.int.name}), " +
                "UNIQUE (${table.nullableInt.name}, ${table.nullableString.name}))"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test
    fun `create table with lots of conditions`() {
        val foreignTable1 = ForeignTableForTest("foreign_table_1")
        val foreignTable2 = ForeignTableForTest("foreign_table_2")
        val table = object : BaseEnhancedTableForTest() {
            val id = col(ModelForTest::int, isId = true, autoIncrement = true)
            val int = foreignCol(ModelForTest::int, foreignColumn = foreignTable1.col)
            val string = col(ModelForTest::string, maxSize = 128, isNonNull = true, isUnique = true)
            val nullableInt = col(ModelForTest::nullableInt)
            val nullableString = foreignCol(
                    ModelForTest::nullableString, foreignColumn = foreignTable2.col,
                    isNonNull = true, autoIncrement = true
            )

            init {
                addUniquenessConstraint(string, nullableInt)
                addUniquenessConstraint(nullableString, nullableInt)
            }
        }

        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.id.name} DUMMY_TYPE AUTO_INCREMENT, " +
                "${table.int.name} DUMMY_TYPE, " +
                "${table.string.name} DUMMY_TYPE(128) NOT NULL, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "${table.nullableString.name} DUMMY_TYPE NOT NULL AUTO_INCREMENT, " +
                "PRIMARY KEY (${table.id.name}), " +
                "UNIQUE (${table.string.name}), " +
                "UNIQUE (${table.string.name}, ${table.nullableInt.name}), " +
                "UNIQUE (${table.nullableString.name}, ${table.nullableInt.name}), " +
                "FOREIGN KEY (${table.int.name}) REFERENCES ${foreignTable1.name}(${foreignTable1.col.name}), " +
                "FOREIGN KEY (${table.nullableString.name}) REFERENCES ${foreignTable2.name}(${foreignTable2.col.name}))"

        assertThat(actualCreateTableSql).containsOnly(expectedCreateTableSql)
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #col after accessing allColumns throws`() {
        `adding columns via #col too late throws` { it.allColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #foreignCol after accessing allColumns throws`() {
        `adding columns via #foreignCol too late throws` { it.allColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addColumn after accessing allColumns throws`() {
        `adding columns via #addColumn too late throws` { it.allColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addForeignColumn after accessing allColumns throws`() {
        `adding columns via #addForeignColumn too late throws` { it.allColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #col after accessing idColumns throws`() {
        `adding columns via #col too late throws` { it.idColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #foreignCol after accessing idColumns throws`() {
        `adding columns via #foreignCol too late throws` { it.idColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addColumn after accessing idColumns throws`() {
        `adding columns via #addColumn too late throws` { it.idColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addForeignColumn after accessing idColumns throws`() {
        `adding columns via #addForeignColumn too late throws` { it.idColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #col after accessing nonIdColumns throws`() {
        `adding columns via #col too late throws` { it.nonIdColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #foreignCol after accessing nonIdColumns throws`() {
        `adding columns via #foreignCol too late throws` { it.nonIdColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addColumn after accessing nonIdColumns throws`() {
        `adding columns via #addColumn too late throws` { it.nonIdColumns }
    }


    @Test(expected = IllegalStateException::class)
    fun `adding columns via #addForeignColumn after accessing nonIdColumns throws`() {
        `adding columns via #addForeignColumn too late throws` { it.nonIdColumns }
    }


    private fun `adding columns via #col too late throws`(
            latenessCausingOp: (BaseEnhancedTableForTest) -> Any?) {

        val tableForTest = object : BaseEnhancedTableForTest() {
            init { col(ModelForTest::int) }
        }

        latenessCausingOp.invoke(tableForTest)
        tableForTest.col(ModelForTest::string)
    }


    private fun `adding columns via #foreignCol too late throws`(
            latenessCausingOp: (BaseEnhancedTableForTest) -> Any?) {

        val tableForTest = object : BaseEnhancedTableForTest() { init { col(ModelForTest::int) } }
        val foreignTableForTest = object : BaseEnhancedTableForTest() {
            val col = col(ModelForTest::int)
        }

        latenessCausingOp.invoke(tableForTest)
        tableForTest.foreignCol(ModelForTest::string, foreignColumn = foreignTableForTest.col)
    }


    private fun `adding columns via #addColumn too late throws`(
            latenessCausingOp: (BaseEnhancedTableForTest) -> Any?) {

        val tableForTest = object : BaseEnhancedTableForTest() {
            init { col(ModelForTest::int) }
        }

        latenessCausingOp.invoke(tableForTest)
        tableForTest.addColumn<String>(
                "string", false, null, false, false, false, mock(), mock()
        )
    }


    private fun `adding columns via #addForeignColumn too late throws`(
            latenessCausingOp: (BaseEnhancedTableForTest) -> Any?) {

        val tableForTest = object : BaseEnhancedTableForTest() {
            init { col(ModelForTest::int) }
        }

        latenessCausingOp.invoke(tableForTest)
        tableForTest.addForeignColumn<String, Unit, Unit>(
                "string", false, null, false, false, false, mock(), mock(), mock()
        )
    }



    private class ModelForTest(
        val int: Int = 0,
        val string: String = "test",
        val nullableInt: Int? = null,
        val nullableString: String? = null
    )



    private abstract class BaseEnhancedTableForTest(name: String = "test") :
            BaseEnhancedTable<ModelForTest, Int, Dao<ModelForTest, Int>>(
                    name, tableConfigurationImpl, simpleCreateTableSqlBuilder) {

        override val dao: Dao<ModelForTest, Int> get() = throw UnsupportedOperationException()
        override fun <C> extractFrom(id: Int, column: Column<ModelForTest, C>) = throw exception()
        override fun create(value: Table.Value<ModelForTest>) = throw exception()

        private fun exception() = UnsupportedOperationException("not implemented")
    }



    private class ForeignTableForTest(name: String = "f_test") : BaseEnhancedTableForTest(name) {
        val col = col(ModelForTest::int)
    }



    companion object {
        private val dummyTypeTranslator = object : TypeTranslator {
            override fun translate(type: Type): String = "DUMMY_TYPE"
            override fun translate(type: Type, maxLength: Int): String = translate(type)
        }

        private val tableConfigurationImpl: TableConfiguration
        private val simpleCreateTableSqlBuilder = H2CreateTableSqlBuilder()

        init {
            tableConfigurationImpl = TableConfigurationImpl(mock(), dummyTypeTranslator)
            tableConfigurationImpl.registerDefaultConverters()
        }
    }
}