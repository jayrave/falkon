package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypeTranslator
import com.jayrave.falkon.sqlBuilders.Dialect
import com.jayrave.falkon.sqlBuilders.SimpleCreateTableSqlBuilder
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BaseEnhancedTableTest {

    @Test
    fun testCreateTableWithoutForeignColumn() {
        class TableForTest : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            val nullableInt = col(ModelForTest::nullableInt)
        }

        val table = TableForTest()
        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.idColumn.name} DUMMY_TYPE, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "PRIMARY KEY (${table.idColumn.name}))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithForeignColumn() {
        class ForeignTableForTest : BaseEnhancedTableForTest("foreign_table") {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "foreign_table_pk_column"
            )
        }

        val foreignTable = ForeignTableForTest()
        class TableForTest : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            val int = foreignCol(ModelForTest::int, foreignColumn = foreignTable.idColumn)
        }

        val table = TableForTest()
        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.idColumn.name} DUMMY_TYPE, " +
                "${table.int.name} DUMMY_TYPE, " +
                "PRIMARY KEY (${table.idColumn.name}), " +
                "FOREIGN KEY (${table.int.name}) REFERENCES ${foreignTable.name}(${foreignTable.idColumn.name}))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithColumnHavingMaxSize() {
        class TableForTest : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            val nullableInt = col(ModelForTest::nullableInt, maxSize = 24)
        }

        val table = TableForTest()
        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.idColumn.name} DUMMY_TYPE, " +
                "${table.nullableInt.name} DUMMY_TYPE(24), " +
                "PRIMARY KEY (${table.idColumn.name}))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithNonNullColumn() {
        class TableForTest : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            val string = col(ModelForTest::string, isNonNull = true)
        }

        val table = TableForTest()
        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.idColumn.name} DUMMY_TYPE, " +
                "${table.string.name} DUMMY_TYPE NOT NULL, " +
                "PRIMARY KEY (${table.idColumn.name}))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithUniquenessConstraints() {
        class TableForTest : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            val int = col(ModelForTest::int, isUnique = true)
            val nullableInt = col(ModelForTest::nullableInt)
            val nullableString = col(ModelForTest::nullableString)

            init {
                addUniquenessConstraint(nullableInt, nullableString)
            }
        }

        val table = TableForTest()
        val actualCreateTableSql = TableForTest().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.idColumn.name} DUMMY_TYPE, " +
                "${table.int.name} DUMMY_TYPE, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "${table.nullableString.name} DUMMY_TYPE, " +
                "PRIMARY KEY (${table.idColumn.name}), " +
                "UNIQUE (${table.int.name}), " +
                "UNIQUE (${table.nullableInt.name}, ${table.nullableString.name}))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithLotsOfConditions() {
        class ForeignTableForTest1 : BaseEnhancedTableForTest("foreign_table_1") {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "foreign_table_1_pk_column"
            )
        }

        class ForeignTableForTest2 : BaseEnhancedTableForTest("foreign_table_2") {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "foreign_table_2_pk_column"
            )
        }

        val foreignTable1 = ForeignTableForTest1()
        val foreignTable2 = ForeignTableForTest2()
        class TableForTest : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "id", autoIncrement = true
            )

            val int = foreignCol(ModelForTest::int, foreignColumn = foreignTable1.idColumn)
            val string = col(ModelForTest::string, maxSize = 128, isNonNull = true, isUnique = true)
            val nullableInt = col(ModelForTest::nullableInt)
            val nullableString = foreignCol(
                    ModelForTest::nullableString, foreignColumn = foreignTable2.idColumn,
                    isNonNull = true, autoIncrement = true
            )

            init {
                addUniquenessConstraint(string, nullableInt)
                addUniquenessConstraint(nullableString, nullableInt)
            }
        }

        val table = TableForTest()
        val actualCreateTableSql = table.buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE ${table.name} (" +
                "${table.idColumn.name} DUMMY_TYPE ${DialectForTesting.autoIncrementExpression}, " +
                "${table.int.name} DUMMY_TYPE, " +
                "${table.string.name} DUMMY_TYPE(128) NOT NULL, " +
                "${table.nullableInt.name} DUMMY_TYPE, " +
                "${table.nullableString.name} DUMMY_TYPE NOT NULL ${DialectForTesting.autoIncrementExpression}, " +
                "PRIMARY KEY (${table.idColumn.name}), " +
                "UNIQUE (${table.string.name}), " +
                "UNIQUE (${table.string.name}, ${table.nullableInt.name}), " +
                "UNIQUE (${table.nullableString.name}, ${table.nullableInt.name}), " +
                "FOREIGN KEY (${table.int.name}) REFERENCES ${foreignTable1.name}(${foreignTable1.idColumn.name}), " +
                "FOREIGN KEY (${table.nullableString.name}) REFERENCES ${foreignTable2.name}(${foreignTable2.idColumn.name}))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
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
        override fun create(value: Value<ModelForTest>): ModelForTest {
            throw UnsupportedOperationException("not implemented")
        }
    }



    object DialectForTesting : Dialect {
        override val autoIncrementExpression: String = "AUTO_INCREMENT_FOR_TESTING"
    }



    companion object {
        private val dummyTypeTranslator = object : TypeTranslator {
            override fun translate(type: Type): String = "DUMMY_TYPE"
            override fun translate(type: Type, maxLength: Int): String = translate(type)
        }

        private val tableConfigurationImpl: TableConfiguration
        private val simpleCreateTableSqlBuilder = SimpleCreateTableSqlBuilder(DialectForTesting)

        init {
            tableConfigurationImpl = TableConfigurationImpl(mock(), dummyTypeTranslator)
            tableConfigurationImpl.registerDefaultConverters()
        }
    }
}