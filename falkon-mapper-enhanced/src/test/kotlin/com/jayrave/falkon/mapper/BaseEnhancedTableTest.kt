package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypeTranslator
import com.jayrave.falkon.sqlBuilders.SimpleCreateTableSqlBuilder
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BaseEnhancedTableTest {

    @Test
    fun testCreateTableWithoutForeignColumn() {
        class Test : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            @Suppress("unused") val nullableInt = col(ModelForTest::nullableInt)
        }

        val actualCreateTableSql = Test().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE test (" +
                "id DUMMY_TYPE, " +
                "nullable_int DUMMY_TYPE, " +
                "PRIMARY KEY (id))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithForeignColumn() {
        class ForeignTable : BaseEnhancedTableForTest("foreign_table") {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "foreign_table_pk_column"
            )
        }

        val foreignTable = ForeignTable()
        class Test : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            @Suppress("unused") val int = foreignCol(
                    ModelForTest::int, foreignTable = foreignTable,
                    foreignColumn = foreignTable.idColumn
            )
        }

        val actualCreateTableSql = Test().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE test (" +
                "id DUMMY_TYPE, " +
                "int DUMMY_TYPE, " +
                "PRIMARY KEY (id), " +
                "FOREIGN KEY (int) REFERENCES foreign_table(foreign_table_pk_column))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithColumnHavingMaxSize() {
        class Test : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            @Suppress("unused") val nullableInt = col(ModelForTest::nullableInt, maxSize = 24)
        }

        val actualCreateTableSql = Test().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE test (" +
                "id DUMMY_TYPE, " +
                "nullable_int DUMMY_TYPE(24), " +
                "PRIMARY KEY (id))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithNonNullColumn() {
        class Test : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            @Suppress("unused") val string = col(ModelForTest::string, isNonNull = true)
        }

        val actualCreateTableSql = Test().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE test (" +
                "id DUMMY_TYPE, " +
                "string DUMMY_TYPE NOT NULL, " +
                "PRIMARY KEY (id))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithUniquenessConstraints() {
        class Test : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            @Suppress("unused") val int = col(ModelForTest::int, isUnique = true)
            val nullableInt = col(ModelForTest::nullableInt)
            val nullableString = col(ModelForTest::nullableString)

            init {
                addUniquenessConstraint(nullableInt, nullableString)
            }
        }

        val actualCreateTableSql = Test().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE test (" +
                "id DUMMY_TYPE, " +
                "int DUMMY_TYPE, " +
                "nullable_int DUMMY_TYPE, " +
                "nullable_string DUMMY_TYPE, " +
                "PRIMARY KEY (id), " +
                "UNIQUE (int), " +
                "UNIQUE (nullable_int, nullable_string))"

        assertThat(actualCreateTableSql).isEqualTo(expectedCreateTableSql)
    }


    @Test
    fun testCreateTableWithLotsOfConditions() {
        class ForeignTable1 : BaseEnhancedTableForTest("foreign_table_1") {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "foreign_table_pk_column"
            )
        }

        class ForeignTable2 : BaseEnhancedTableForTest("foreign_table_2") {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(
                    ModelForTest::int, "foreign_table_pk_column"
            )
        }

        val foreignTable1 = ForeignTable1()
        val foreignTable2 = ForeignTable2()
        class Test : BaseEnhancedTableForTest() {
            override val idColumn: EnhancedColumn<ModelForTest, Int> = col(ModelForTest::int, "id")
            @Suppress("unused") val int = foreignCol(
                    ModelForTest::int, foreignTable = foreignTable1,
                    foreignColumn = foreignTable1.idColumn
            )

            val string = col(ModelForTest::string, maxSize = 128, isNonNull = true, isUnique = true)
            val nullableInt = col(ModelForTest::nullableInt)
            @Suppress("unused") val nullableString = foreignCol(
                    ModelForTest::nullableString, foreignTable = foreignTable2,
                    foreignColumn = foreignTable2.idColumn, isNonNull = true
            )

            init {
                addUniquenessConstraint(string, nullableInt)
                addUniquenessConstraint(nullableString, nullableInt)
            }
        }

        val actualCreateTableSql = Test().buildCreateTableSql()
        val expectedCreateTableSql = "CREATE TABLE test (" +
                "id DUMMY_TYPE, " +
                "int DUMMY_TYPE, " +
                "string DUMMY_TYPE(128) NOT NULL, " +
                "nullable_int DUMMY_TYPE, " +
                "nullable_string DUMMY_TYPE NOT NULL, " +
                "PRIMARY KEY (id), " +
                "UNIQUE (string), " +
                "UNIQUE (string, nullable_int), " +
                "UNIQUE (nullable_string, nullable_int), " +
                "FOREIGN KEY (int) REFERENCES foreign_table_1(foreign_table_pk_column), " +
                "FOREIGN KEY (nullable_string) REFERENCES foreign_table_2(foreign_table_pk_column))"

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



    companion object {
        private val dummyTypeTranslator = object : TypeTranslator {
            override fun translate(type: Type): String = "DUMMY_TYPE"
            override fun translate(type: Type, maxLength: Int): String = translate(type)
        }

        private val tableConfigurationImpl: TableConfiguration
        private val simpleCreateTableSqlBuilder = SimpleCreateTableSqlBuilder()

        init {
            tableConfigurationImpl = TableConfigurationImpl(mock(), dummyTypeTranslator)
            tableConfigurationImpl.registerDefaultConverters()
        }
    }
}