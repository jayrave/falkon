package com.jayrave.falkon.engine.test

import org.assertj.core.api.Assertions.assertThat

abstract class BaseClassForTestingCompilingSqlWithAllTypesOfBoundArgs(
        protected val nativeSqlExecutor: NativeSqlExecutor,
        protected val nativeQueryExecutor: NativeQueryExecutor) {

    protected fun createTableWithColumnsForAllTypesUsingNativeMethods() {
        nativeSqlExecutor.execute(
                "CREATE TABLE test (" +
                        "column_name_short SMALLINT, " +
                        "column_name_int INTEGER, " +
                        "column_name_long BIGINT, " +
                        "column_name_float REAL, " +
                        "column_name_double DOUBLE, " +
                        "column_name_string VARCHAR, " +
                        "column_name_blob BLOB" +
                        ")"
        )
    }


    protected fun getSqlToInsertOneRowWithAllTypesWithPlaceholders(): String {
        return "INSERT INTO test (" +
                "column_name_short, column_name_int, column_name_long, " +
                "column_name_float, column_name_double, column_name_string, " +
                "column_name_blob) VALUES (?, ?, ?, ?, ?, ?, ?)"
    }


    protected fun assertOneRowWithAllTypesUsingDataSource(
            short: Short, int: Int, long: Long, float: Float, double: Double,
            string: String, blob: ByteArray) {

        val source = nativeQueryExecutor.execute("SELECT * FROM test")
        assertThat(source.moveToFirst()).isTrue()
        assertThat(source.getShort(source.getColumnIndex("column_name_short"))).isEqualTo(short)
        assertThat(source.getInt(source.getColumnIndex("column_name_int"))).isEqualTo(int)
        assertThat(source.getLong(source.getColumnIndex("column_name_long"))).isEqualTo(long)
        assertThat(source.getFloat(source.getColumnIndex("column_name_float"))).isEqualTo(float)
        assertThat(source.getDouble(source.getColumnIndex("column_name_double"))).isEqualTo(double)
        assertThat(source.getString(source.getColumnIndex("column_name_string"))).isEqualTo(string)
        assertThat(source.getBlob(source.getColumnIndex("column_name_blob"))).isEqualTo(blob)
        assertThat(source.moveToNext()).isFalse()
    }
}