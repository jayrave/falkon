package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.engine.DefaultEngine
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.jdbc.JdbcEngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import com.jayrave.falkon.mapper.*
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import javax.sql.DataSource
import com.jayrave.falkon.engine.CompiledStatement as CS

abstract class BaseClassForCompiledQueryExtnTests {

    private lateinit var dataSource: DataSource
    protected lateinit var engine: Engine

    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        val ds = JdbcConnectionPool.create("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1", "user", "pw")
        ds.loginTimeout = 1
        ds.maxConnections = 1

        dataSource = ds
        engine = DefaultEngine(JdbcEngineCore(dataSource))

        // Create table
        engine.compileSql(
                listOf(TABLE_NAME),
                "CREATE TABLE $TABLE_NAME($INT_COL_NAME INTEGER, $STRING_COL_NAME VARCHAR)"
        ).safeCloseAfterExecution()
    }


    @After
    fun tearDown() {
        // http://www.h2database.com/html/grammar.html#shutdown
        // http://www.h2database.com/html/features.html#in_memory_databases
        // By default h2 closes the database when all existing connections to it are closed.
        // For an in-memory db, closing is akin to nuking it. This makes sure that we have a
        // clean slate for every test
        dataSource.connection.prepareStatement("SHUTDOWN").execute()
    }


    protected fun clearTestTable() {
        engine
                .compileDelete(TABLE_NAME, "DELETE FROM $TABLE_NAME")
                .safeCloseAfterExecution()
    }


    protected fun insertRow(seed: Int) {
        engine
                .compileInsert(TABLE_NAME, "INSERT INTO $TABLE_NAME VALUES(?, ?)")
                .bindInt(1, seed)
                .bindString(2, "test $seed")
                .safeCloseAfterExecution()
    }



    protected class ModelForTest(val int: Int, val string: String)



    protected class TableForTest : Table<ModelForTest, Int> {
        override val name: String get() = TABLE_NAME
        override val configuration: TableConfiguration get() = throw exception
        override val allColumns: Collection<Column<ModelForTest, *>> get() = throw exception
        override val idColumns: Collection<Column<ModelForTest, *>> get() = throw exception
        override val nonIdColumns: Collection<Column<ModelForTest, *>> get() = throw exception
        override fun <C> extractFrom(id: Int, column: Column<ModelForTest, C>) = throw exception

        val intCol = ColumnForTest(INT_COL_NAME, { it.getInt() })
        val stringCol = ColumnForTest(STRING_COL_NAME, { it.getString() })

        override fun create(value: Table.Value<ModelForTest>): ModelForTest {
            return ModelForTest(value of intCol, value of stringCol)
        }
    }



    protected class ColumnForTest<C>(
            override val name: String, private val propertyExtractor: (DataProducer) -> C) :
            Column<ModelForTest, C> {

        override val table: Table<ModelForTest, *> get() = throw exception
        override val dbType: Type get() = throw exception
        override val isId: Boolean get() = throw exception

        override fun extractPropertyFrom(t: ModelForTest) = throw exception
        override fun computeStorageFormOf(property: C) = throw exception
        override fun putStorageFormIn(property: C, dataConsumer: DataConsumer) = throw exception

        override fun computePropertyFrom(dataProducer: DataProducer): C {
            return propertyExtractor.invoke(dataProducer)
        }
    }



    companion object {
        const val TABLE_NAME = "test"
        const val INT_COL_NAME = "int"
        const val STRING_COL_NAME = "string"

        private val exception = UnsupportedOperationException()
    }
}