package com.jayrave.falkon.engine.test

import org.assertj.core.api.Assertions.assertThat

abstract class BaseClassForTestingSqlWithAllTypesOfBoundArgs(
        protected val nativeSqlExecutor: NativeSqlExecutor,
        protected val nativeQueryExecutor: NativeQueryExecutor) {

    protected fun createTableWithColumnsForAllTypesUsingNativeMethod() {
        nativeSqlExecutor.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$SHORT_COLUMN_NAME SMALLINT, " +
                        "$INT_COLUMN_NAME INTEGER, " +
                        "$LONG_COLUMN_NAME BIGINT, " +
                        "$FLOAT_COLUMN_NAME REAL, " +
                        "$DOUBLE_COLUMN_NAME DOUBLE, " +
                        "$STRING_COLUMN_NAME VARCHAR, " +
                        "$BLOB_COLUMN_NAME BLOB, " +
                        "$NULLABLE_SHORT_COLUMN_NAME SMALLINT, " +
                        "$NULLABLE_INT_COLUMN_NAME INTEGER, " +
                        "$NULLABLE_LONG_COLUMN_NAME BIGINT, " +
                        "$NULLABLE_FLOAT_COLUMN_NAME REAL, " +
                        "$NULLABLE_DOUBLE_COLUMN_NAME DOUBLE, " +
                        "$NULLABLE_STRING_COLUMN_NAME VARCHAR, " +
                        "$NULLABLE_BLOB_COLUMN_NAME BLOB" +
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
                "$BLOB_COLUMN_NAME, " +
                "$NULLABLE_SHORT_COLUMN_NAME, " +
                "$NULLABLE_INT_COLUMN_NAME, " +
                "$NULLABLE_LONG_COLUMN_NAME, " +
                "$NULLABLE_FLOAT_COLUMN_NAME, " +
                "$NULLABLE_DOUBLE_COLUMN_NAME, " +
                "$NULLABLE_STRING_COLUMN_NAME, " +
                "$NULLABLE_BLOB_COLUMN_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    }


    protected fun assertOneRowWithAllTypesUsingNativeMethod(
            short: Short, int: Int, long: Long, float: Float,
            double: Double, string: String, blob: ByteArray,
            nullableShort: Short?, nullableInt: Int?, nullableLong: Long?, nullableFloat: Float?,
            nullableDouble: Double?, nullableString: String?, nullableBlob: ByteArray?) {

        val source = nativeQueryExecutor.execute("SELECT * FROM $TABLE_NAME")
        assertThat(source.moveToFirst()).isTrue()
        assertThat(source.getShort(source.getColumnIndex(SHORT_COLUMN_NAME))).isEqualTo(short)
        assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(int)
        assertThat(source.getLong(source.getColumnIndex(LONG_COLUMN_NAME))).isEqualTo(long)
        assertThat(source.getFloat(source.getColumnIndex(FLOAT_COLUMN_NAME))).isEqualTo(float)
        assertThat(source.getDouble(source.getColumnIndex(DOUBLE_COLUMN_NAME))).isEqualTo(double)
        assertThat(source.getString(source.getColumnIndex(STRING_COLUMN_NAME))).isEqualTo(string)
        assertThat(source.getBlob(source.getColumnIndex(BLOB_COLUMN_NAME))).isEqualTo(blob)

        val nullableShortIndex = source.getColumnIndex(NULLABLE_SHORT_COLUMN_NAME)
        when (nullableShort) {
            null -> assertThat(source.isNull(nullableShortIndex)).isTrue()
            else -> assertThat(source.getShort(nullableShortIndex)).isEqualTo(nullableShort)
        }

        val nullableIntIndex = source.getColumnIndex(NULLABLE_INT_COLUMN_NAME)
        when (nullableInt) {
            null -> assertThat(source.isNull(nullableIntIndex)).isTrue()
            else -> assertThat(source.getInt(nullableIntIndex)).isEqualTo(nullableInt)
        }

        val nullableLongIndex = source.getColumnIndex(NULLABLE_LONG_COLUMN_NAME)
        when (nullableLong) {
            null -> assertThat(source.isNull(nullableLongIndex)).isTrue()
            else -> assertThat(source.getLong(nullableLongIndex)).isEqualTo(nullableLong)
        }

        val nullableFloatIndex = source.getColumnIndex(NULLABLE_FLOAT_COLUMN_NAME)
        when (nullableFloat) {
            null -> assertThat(source.isNull(nullableFloatIndex)).isTrue()
            else -> assertThat(source.getFloat(nullableFloatIndex)).isEqualTo(nullableFloat)
        }

        val nullableDoubleIndex = source.getColumnIndex(NULLABLE_DOUBLE_COLUMN_NAME)
        when (nullableDouble) {
            null -> assertThat(source.isNull(nullableDoubleIndex)).isTrue()
            else -> assertThat(source.getDouble(nullableDoubleIndex)).isEqualTo(nullableDouble)
        }

        val nullableStringIndex = source.getColumnIndex(NULLABLE_STRING_COLUMN_NAME)
        when (nullableString) {
            null -> assertThat(source.isNull(nullableStringIndex)).isTrue()
            else -> assertThat(source.getString(nullableStringIndex)).isEqualTo(nullableString)
        }

        val nullableBlobIndex = source.getColumnIndex(NULLABLE_BLOB_COLUMN_NAME)
        when (nullableBlob) {
            null -> assertThat(source.isNull(nullableBlobIndex)).isTrue()
            else -> assertThat(source.getBlob(nullableBlobIndex)).isEqualTo(nullableBlob)
        }

        assertThat(source.moveToNext()).isFalse()
        source.close()
    }



    companion object {
        const val TABLE_NAME = "test"
        const val SHORT_COLUMN_NAME = "column_short"
        const val INT_COLUMN_NAME = "column_int"
        const val LONG_COLUMN_NAME = "column_long"
        const val FLOAT_COLUMN_NAME = "column_float"
        const val DOUBLE_COLUMN_NAME = "column_double"
        const val STRING_COLUMN_NAME = "column_string"
        const val BLOB_COLUMN_NAME = "column_blob"
        const val NULLABLE_SHORT_COLUMN_NAME = "nullable_column_short"
        const val NULLABLE_INT_COLUMN_NAME = "nullable_column_int"
        const val NULLABLE_LONG_COLUMN_NAME = "nullable_column_long"
        const val NULLABLE_FLOAT_COLUMN_NAME = "nullable_column_float"
        const val NULLABLE_DOUBLE_COLUMN_NAME = "nullable_column_double"
        const val NULLABLE_STRING_COLUMN_NAME = "nullable_column_string"
        const val NULLABLE_BLOB_COLUMN_NAME = "nullable_column_blob"
    }
}