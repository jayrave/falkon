package com.jayrave.falkon.engine.test

import org.assertj.core.api.Assertions.assertThat

abstract class BaseClassForTestingCompilingSqlWithAllTypesOfBoundArgs(
        protected val nativeSqlExecutor: NativeSqlExecutor,
        protected val nativeQueryExecutor: NativeQueryExecutor) {

    protected fun createTableWithColumnsForAllTypesUsingNativeMethods() {
        nativeSqlExecutor.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$SHORT_COLUMN_NAME SMALLINT, " +
                        "$INT_COLUMN_NAME INTEGER, " +
                        "$LONG_COLUMN_NAME BIGINT, " +
                        "$FLOAT_COLUMN_NAME REAL, " +
                        "$DOUBLE_COLUMN_NAME DOUBLE, " +
                        "$STRING_COLUMN_NAME VARCHAR, " +
                        "$BLOB_COLUMN_NAME BLOB" +
                        ")"
        )
    }


    protected fun getSqlToInsertOneRowWithAllTypesWithPlaceholders(): String {
        return "INSERT INTO $TABLE_NAME (" +
                "$SHORT_COLUMN_NAME, " +
                "$INT_COLUMN_NAME, " +
                "$LONG_COLUMN_NAME, " +
                "$FLOAT_COLUMN_NAME, " +
                "$DOUBLE_COLUMN_NAME, " +
                "$STRING_COLUMN_NAME, " +
                "$BLOB_COLUMN_NAME) VALUES (?, ?, ?, ?, ?, ?, ?)"
    }


    protected fun assertOneRowWithAllTypesUsingDataSource(
            short: Short, int: Int, long: Long, float: Float, double: Double,
            string: String, blob: ByteArray) {

        val source = nativeQueryExecutor.execute("SELECT * FROM $TABLE_NAME")
        assertThat(source.moveToFirst()).isTrue()
        assertThat(source.getShort(source.getColumnIndex(SHORT_COLUMN_NAME))).isEqualTo(short)
        assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(int)
        assertThat(source.getLong(source.getColumnIndex(LONG_COLUMN_NAME))).isEqualTo(long)
        assertThat(source.getFloat(source.getColumnIndex(FLOAT_COLUMN_NAME))).isEqualTo(float)
        assertThat(source.getDouble(source.getColumnIndex(DOUBLE_COLUMN_NAME))).isEqualTo(double)
        assertThat(source.getString(source.getColumnIndex(STRING_COLUMN_NAME))).isEqualTo(string)
        assertThat(source.getBlob(source.getColumnIndex(BLOB_COLUMN_NAME))).isEqualTo(blob)
        assertThat(source.moveToNext()).isFalse()
    }



    companion object {
        @JvmStatic protected val TABLE_NAME = "test"
        @JvmStatic protected val SHORT_COLUMN_NAME = "column_name_short"
        @JvmStatic protected val INT_COLUMN_NAME = "column_name_int"
        @JvmStatic protected val LONG_COLUMN_NAME = "column_name_long"
        @JvmStatic protected val FLOAT_COLUMN_NAME = "column_name_float"
        @JvmStatic protected val DOUBLE_COLUMN_NAME = "column_name_double"
        @JvmStatic protected val STRING_COLUMN_NAME = "column_name_string"
        @JvmStatic protected val BLOB_COLUMN_NAME = "column_name_blob"
    }
}