package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestCompileInsertWithAllTypesOfBoundArgs(
        private val engineCore: EngineCore, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun `perform test`() {
        createTableWithColumnsForAllTypesUsingNativeMethod()

        // Insert using engine
        val numberOfRowsAffected = engineCore
                .compileInsert(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .bindNull(8, Type.SHORT)
                .bindNull(9, Type.INT)
                .bindNull(10, Type.LONG)
                .bindNull(11, Type.FLOAT)
                .bindNull(12, Type.DOUBLE)
                .bindNull(13, Type.STRING)
                .bindNull(14, Type.BLOB)
                .safeCloseAfterExecution()

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertOneRowWithAllTypesUsingNativeMethod(
                5, 6, 7, 8F, 9.0, "test 10", byteArrayOf(11),
                null, null, null, null, null, null, null
        )
    }
}