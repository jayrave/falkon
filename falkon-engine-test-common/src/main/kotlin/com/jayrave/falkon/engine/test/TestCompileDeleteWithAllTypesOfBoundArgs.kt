package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat

class TestCompileDeleteWithAllTypesOfBoundArgs private constructor(
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

        // Execute delete using engine
        val numberOfRowsAffected = engineCore.compileDelete(
                "DELETE FROM $TABLE_NAME WHERE " +
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

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertThat(nativeQueryExecutor.getCount(TABLE_NAME)).isEqualTo(0)
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileDeleteWithAllTypesOfBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}