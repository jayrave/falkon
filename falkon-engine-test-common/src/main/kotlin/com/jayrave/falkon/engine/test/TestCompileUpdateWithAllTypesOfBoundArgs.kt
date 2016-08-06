package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat

class TestCompileUpdateWithAllTypesOfBoundArgs private constructor(
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

        // Execute update using engine
        val numberOfRowsAffected = engineCore.compileUpdate(
                "UPDATE $TABLE_NAME SET " +
                        "$SHORT_COLUMN_NAME = ?, " +
                        "$INT_COLUMN_NAME = ?, " +
                        "$LONG_COLUMN_NAME = ?, " +
                        "$FLOAT_COLUMN_NAME = ?, " +
                        "$DOUBLE_COLUMN_NAME = ?, " +
                        "$STRING_COLUMN_NAME = ?, " +
                        "$BLOB_COLUMN_NAME = ? " +
                        "WHERE " +
                        "$SHORT_COLUMN_NAME = ? AND " +
                        "$INT_COLUMN_NAME = ? AND " +
                        "$LONG_COLUMN_NAME = ? AND " +
                        "$FLOAT_COLUMN_NAME = ? AND " +
                        "$DOUBLE_COLUMN_NAME = ? AND " +
                        "$STRING_COLUMN_NAME = ? AND " +
                        "$BLOB_COLUMN_NAME = ?"
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
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileUpdateWithAllTypesOfBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}