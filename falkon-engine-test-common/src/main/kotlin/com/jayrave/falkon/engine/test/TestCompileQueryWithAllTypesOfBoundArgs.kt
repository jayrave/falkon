package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat

class TestCompileQueryWithAllTypesOfBoundArgs private constructor(
        private val engineCore: EngineCore, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingCompilingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun performTest() {
        createTableWithColumnsForAllTypesUsingNativeMethods()

        // Execute insert using engine since its easier
        engineCore.compileInsert(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        // Execute query using engine
        val source = engineCore.compileQuery(
                "SELECT * FROM $TABLE_NAME WHERE " +
                        "$SHORT_COLUMN_NAME = ? AND " +
                        "$INT_COLUMN_NAME = ? AND " +
                        "$LONG_COLUMN_NAME = ? AND " +
                        "$FLOAT_COLUMN_NAME = ? AND " +
                        "$DOUBLE_COLUMN_NAME = ? AND " +
                        "$STRING_COLUMN_NAME = ? AND " +
                        "$BLOB_COLUMN_NAME = ?"
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
        assertThat(source.getShort(source.getColumnIndex(SHORT_COLUMN_NAME))).isEqualTo(5)
        assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(6)
        assertThat(source.getLong(source.getColumnIndex(LONG_COLUMN_NAME))).isEqualTo(7)
        assertThat(source.getFloat(source.getColumnIndex(FLOAT_COLUMN_NAME))).isEqualTo(8F)
        assertThat(source.getDouble(source.getColumnIndex(DOUBLE_COLUMN_NAME))).isEqualTo(9.0)
        assertThat(source.getString(source.getColumnIndex(STRING_COLUMN_NAME))).isEqualTo("test 10")
        assertThat(source.getBlob(
                source.getColumnIndex(BLOB_COLUMN_NAME)
        )).isEqualTo(byteArrayOf(11))

        assertThat(source.moveToNext()).isEqualTo(false)
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileQueryWithAllTypesOfBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}