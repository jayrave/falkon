package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileInsertWithAllTypesOfBoundArgs private constructor(
        private val engine: Engine, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingCompilingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun performTest() {
        createTableWithColumnsForAllTypesUsingNativeMethods()
        val numberOfRowsAffected = engine
                .compileInsert(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .execute()

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertOneRowWithAllTypesUsingDataSource(5, 6, 7, 8F, 9.0, "test 10", byteArrayOf(11))
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileInsertWithAllTypesOfBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}