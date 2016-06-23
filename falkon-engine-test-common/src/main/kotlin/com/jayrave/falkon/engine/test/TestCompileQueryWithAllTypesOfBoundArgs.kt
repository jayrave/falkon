package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileQueryWithAllTypesOfBoundArgs private constructor(
        private val engine: Engine, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingCompilingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun performTest() {
        createTableWithColumnsForAllTypesUsingNativeMethods()

        // Execute insert using engine since its easier
        engine.compileInsert(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
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


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileQueryWithAllTypesOfBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}