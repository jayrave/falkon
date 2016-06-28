package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.ModelForTest
import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.defaultTableConfiguration
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.jdbc.JdbcEngine
import org.assertj.core.api.Assertions.assertThat
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import java.util.*
import javax.sql.DataSource

/**
 * Before every test, a clean database that just has an empty `test` table that corresponds to
 * [TableForTest] is setup
 */
abstract class BaseClassForIntegrationTests {

    private lateinit var dataSource: DataSource
    protected lateinit var engine: Engine
    internal lateinit var table: TableForTest

    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=0", "user", "pw")
        engine = JdbcEngine(dataSource)
        table = TableForTest(defaultTableConfiguration(engine))

        engine.createTableForTest()
    }


    @After
    fun tearDown() {
        // http://www.h2database.com/html/grammar.html#shutdown
        // http://www.h2database.com/html/features.html#in_memory_databases
        // By default h2 closes the database when all existing connections to it are closed.
        // This makes sure that we have a clean slate for every test
        dataSource.connection.prepareStatement("SHUTDOWN").execute()
    }


    companion object {

        private fun Engine.createTableForTest() {
            this.compileSql(
                    "CREATE TABLE test (" +
                            "id VARCHAR PRIMARY KEY, " +
                            "short SMALLINT NOT NULL, " +
                            "int INTEGER NOT NULL, " +
                            "long BIGINT NOT NULL, " +
                            "float REAL NOT NULL, " +
                            "double DOUBLE NOT NULL, " +
                            "string VARCHAR NOT NULL, " +
                            "blob BLOB NOT NULL, " +
                            "nullable_short SMALLINT, " +
                            "nullable_int INTEGER, " +
                            "nullable_long BIGINT, " +
                            "nullable_float REAL, " +
                            "nullable_double DOUBLE, " +
                            "nullable_string VARCHAR, " +
                            "nullable_blob BLOB" +
                            ")"
            ).execute()
        }


        internal fun insertModelUsingInsertBuilder(
                table: TableForTest, model: ModelForTest) {

            table.dao.insertBuilder()
                    .set(table.id, model.id)
                    .set(table.short, model.short)
                    .set(table.int, model.int)
                    .set(table.long, model.long)
                    .set(table.float, model.float)
                    .set(table.double, model.double)
                    .set(table.string, model.string)
                    .set(table.blob, model.blob)
                    .set(table.nullableShort, model.nullableShort)
                    .set(table.nullableInt, model.nullableInt)
                    .set(table.nullableLong, model.nullableLong)
                    .set(table.nullableFloat, model.nullableFloat)
                    .set(table.nullableDouble, model.nullableDouble)
                    .set(table.nullableString, model.nullableString)
                    .set(table.nullableBlob, model.nullableBlob)
                    .insert()
        }


        internal fun insertModelsUsingInsertBuilder(
                table: TableForTest, vararg models: ModelForTest) {
            table.configuration.engine.executeInTransaction {
                models.forEach { insertModelUsingInsertBuilder(table, it) }
            }
        }


        internal fun getNumberOfModelsInTableForTest(table: TableForTest): Int {
            val compiledQuery = table
                    .configuration
                    .engine
                    .compileQuery("SELECT COUNT(*) as count from ${table.name}")

            val source = compiledQuery.execute()

            assertThat(source.moveToNext()).isTrue()
            val count = source.getInt(source.getColumnIndex("count"))

            source.close()
            compiledQuery.close()
            return count
        }


        /**
         * @param seedValue will be used as is for short & every subsequent parameter will
         * be 1 more than the previous parameter
         */
        internal fun buildModelForTest(
                seedValue: Short, id: UUID = UUID.randomUUID()): ModelForTest {

            return ModelForTest(
                    id, short = seedValue, int = seedValue + 1, long = seedValue + 2L,
                    float = seedValue + 3F, double = seedValue + 4.0,
                    string = "test ${seedValue + 5}",
                    blob = byteArrayOf((seedValue + 6).toByte())
            )
        }


        internal fun assertCurrentRowCorrespondsTo(
                source: Source, model: ModelForTest, table: TableForTest) {

            assertThat(source.getString(source.getColumnIndex(table.id.name)))
                    .isEqualTo(model.id.toString())

            assertThat(source.getShort(source.getColumnIndex(table.short.name)))
                    .isEqualTo(model.short)

            assertThat(source.getInt(source.getColumnIndex(table.int.name)))
                    .isEqualTo(model.int)

            assertThat(source.getLong(source.getColumnIndex(table.long.name)))
                    .isEqualTo(model.long)

            assertThat(source.getFloat(source.getColumnIndex(table.float.name)))
                    .isEqualTo(model.float)

            assertThat(source.getDouble(source.getColumnIndex(table.double.name)))
                    .isEqualTo(model.double)

            assertThat(source.getString(source.getColumnIndex(table.string.name)))
                    .isEqualTo(model.string)

            assertThat(source.getBlob(source.getColumnIndex(table.blob.name)))
                    .isEqualTo(model.blob)

            assertThat(source.isNull(source.getColumnIndex(table.nullableShort.name))).isTrue()
            assertThat(source.isNull(source.getColumnIndex(table.nullableInt.name))).isTrue()
            assertThat(source.isNull(source.getColumnIndex(table.nullableLong.name))).isTrue()
            assertThat(source.isNull(source.getColumnIndex(table.nullableFloat.name))).isTrue()
            assertThat(source.isNull(source.getColumnIndex(table.nullableDouble.name))).isTrue()
            assertThat(source.isNull(source.getColumnIndex(table.nullableString.name))).isTrue()
            assertThat(source.isNull(source.getColumnIndex(table.nullableBlob.name))).isTrue()
        }
    }
}