package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileUpdateWithAllTypesOfBoundArgs private constructor(
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


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileUpdateWithAllTypesOfBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}