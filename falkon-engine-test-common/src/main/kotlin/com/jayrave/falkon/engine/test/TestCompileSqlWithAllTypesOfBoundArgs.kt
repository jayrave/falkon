package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore

class TestCompileSqlWithAllTypesOfBoundArgs private constructor(
        private val engineCore: EngineCore, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingCompilingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun performTest() {
        createTableWithColumnsForAllTypesUsingNativeMethods()
        engineCore.compileSql(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
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


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileSqlWithAllTypesOfBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}