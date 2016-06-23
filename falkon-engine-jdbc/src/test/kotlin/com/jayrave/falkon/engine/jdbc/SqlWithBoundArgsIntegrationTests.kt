package com.jayrave.falkon.engine.jdbc

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SqlWithBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testCompileSqlWithAllTypesOfBoundArgs() {
        // Create table using data source
        createTableWithColumnsForAllTypesUsingDataSource()

        // Execute using engine
        engine.compileSql(SQL_TO_INSERT_ONE_ROW_WITH_ALL_TYPES_WITH_PLACEHOLDERS)
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        assertOneRowWithAllTypesUsingDataSource(5, 6, 7, 8F, 9.0, "test 10", byteArrayOf(11))
    }


    @Test
    fun testCompileInsertWithAllTypesOfBoundArgs() {
        // Create table using data source
        createTableWithColumnsForAllTypesUsingDataSource()

        // Execute using engine
        val numberOfRowsAffected = engine
                .compileInsert(SQL_TO_INSERT_ONE_ROW_WITH_ALL_TYPES_WITH_PLACEHOLDERS)
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertOneRowWithAllTypesUsingDataSource(5, 6, 7, 8F, 9.0, "test 10", byteArrayOf(11))
    }


    @Test
    fun testCompileUpdateWithAllTypesOfBoundArgs() {
        // Create table using data source
        createTableWithColumnsForAllTypesUsingDataSource()

        // Execute insert using engine since its easier
        engine.compileInsert(SQL_TO_INSERT_ONE_ROW_WITH_ALL_TYPES_WITH_PLACEHOLDERS)
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        // Execute update using engine
        val numberOfRowsAffected = engine.compileUpdate(
                "UPDATE test SET " +
                        "column_name_short = ?, column_name_int = ?, column_name_long = ?, " +
                        "column_name_float = ?, column_name_double = ?, column_name_string = ?, " +
                        "column_name_blob = ? " +
                        "WHERE " +
                        "column_name_short = ? AND column_name_int = ? AND " +
                        "column_name_long = ? AND column_name_float = ? AND " +
                        "column_name_double = ? AND column_name_string = ? AND " +
                        "column_name_blob = ?"
        )
                .bindShort(1, 12)
                .bindInt(2, 13)
                .bindLong(3, 14)
                .bindFloat(4, 15F)
                .bindDouble(5, 16.0)
                .bindString(6, "test 17")
                .bindBlob(7, byteArrayOf(18))
                .bindShort(8, 5)
                .bindInt(9, 6)
                .bindLong(10, 7)
                .bindFloat(11, 8F)
                .bindDouble(12, 9.0)
                .bindString(13, "test 10")
                .bindBlob(14, byteArrayOf(11))
                .execute()

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertOneRowWithAllTypesUsingDataSource(12, 13, 14, 15F, 16.0, "test 17", byteArrayOf(18))
    }


    @Test
    fun testCompileDeleteWithAllTypesOfBoundArgs() {
        // Create table using data source
        createTableWithColumnsForAllTypesUsingDataSource()

        // Execute insert using engine since its easier
        engine.compileInsert(SQL_TO_INSERT_ONE_ROW_WITH_ALL_TYPES_WITH_PLACEHOLDERS)
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        // Execute delete using engine
        val numberOfRowsAffected = engine.compileUpdate(
                "DELETE FROM test WHERE " +
                        "column_name_short = ? AND column_name_int = ? AND " +
                        "column_name_long = ? AND column_name_float = ? AND " +
                        "column_name_double = ? AND column_name_string = ? AND " +
                        "column_name_blob = ?"
        )
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertCountUsingDataSource("test", 0)
    }


    @Test
    fun testCompileQueryWithAllTypesOfBoundArgs() {
        // Create table using data source
        createTableWithColumnsForAllTypesUsingDataSource()

        // Execute insert using engine since its easier
        engine.compileInsert(SQL_TO_INSERT_ONE_ROW_WITH_ALL_TYPES_WITH_PLACEHOLDERS)
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        // Execute query using engine
        val source = engine.compileQuery(
                "SELECT * FROM test WHERE " +
                        "column_name_short = ? AND column_name_int = ? AND " +
                        "column_name_long = ? AND column_name_float = ? AND " +
                        "column_name_double = ? AND column_name_string = ? AND " +
                        "column_name_blob = ?"
        )
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        // Assert source's result set
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getShort(source.getColumnIndex("column_name_short"))).isEqualTo(5)
        assertThat(source.getInt(source.getColumnIndex("column_name_int"))).isEqualTo(6)
        assertThat(source.getLong(source.getColumnIndex("column_name_long"))).isEqualTo(7)
        assertThat(source.getFloat(source.getColumnIndex("column_name_float"))).isEqualTo(8F)
        assertThat(source.getDouble(source.getColumnIndex("column_name_double"))).isEqualTo(9.0)
        assertThat(source.getString(source.getColumnIndex("column_name_string")))
                .isEqualTo("test 10")

        assertThat(source.getBlob(source.getColumnIndex("column_name_blob")))
                .isEqualTo(byteArrayOf(11))

        assertThat(source.moveToNext()).isEqualTo(false)
    }


    private fun createTableWithColumnsForAllTypesUsingDataSource() {
        executeStatementUsingDataSource(SQL_TO_CREATE_TABLE_WITH_COLUMNS_FOR_ALL_TYPES)
    }


    private fun assertOneRowWithAllTypesUsingDataSource(
            short: Short, int: Int, long: Long, float: Float, double: Double,
            string: String, blob: ByteArray) {

        val resultSet = dataSource.connection.prepareStatement("SELECT * FROM test").executeQuery()
        assertThat(resultSet.first()).isTrue()
        assertThat(resultSet.getShort("column_name_short")).isEqualTo(short)
        assertThat(resultSet.getInt("column_name_int")).isEqualTo(int)
        assertThat(resultSet.getLong("column_name_long")).isEqualTo(long)
        assertThat(resultSet.getFloat("column_name_float")).isEqualTo(float)
        assertThat(resultSet.getDouble("column_name_double")).isEqualTo(double)
        assertThat(resultSet.getString("column_name_string")).isEqualTo(string)

        val blobFromResult = resultSet.getBlob("column_name_blob")
        assertThat(blobFromResult.getBytes(0, blobFromResult.length().toInt())).isEqualTo(blob)
        assertThat(resultSet.next()).isFalse()
    }


    companion object {
        private const val SQL_TO_CREATE_TABLE_WITH_COLUMNS_FOR_ALL_TYPES =
                "CREATE TABLE test (" +
                        "column_name_short SMALLINT, " +
                        "column_name_int INTEGER, " +
                        "column_name_long BIGINT, " +
                        "column_name_float REAL, " +
                        "column_name_double DOUBLE, " +
                        "column_name_string VARCHAR, " +
                        "column_name_blob BLOB" +
                        ")"

        private const val SQL_TO_INSERT_ONE_ROW_WITH_ALL_TYPES_WITH_PLACEHOLDERS =
                "INSERT INTO test (" +
                        "column_name_short, column_name_int, column_name_long, " +
                        "column_name_float, column_name_double, column_name_string, " +
                        "column_name_blob) VALUES (?, ?, ?, ?, ?, ?, ?)"
    }
}