package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileDeleteWithAllTypesOfBoundArgs private constructor(
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

        // Execute delete using engine
        val numberOfRowsAffected = engine.compileDelete(
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
        assertThat(nativeQueryExecutor.getCount("test")).isEqualTo(0)
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileDeleteWithAllTypesOfBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}