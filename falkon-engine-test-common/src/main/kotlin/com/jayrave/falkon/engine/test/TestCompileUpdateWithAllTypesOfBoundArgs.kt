package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestCompileUpdateWithAllTypesOfBoundArgs(
        private val engineCore: EngineCore, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun `perform test`() {
        createTableWithColumnsForAllTypesUsingNativeMethod()

        // Execute insert using engine since its easier
        engineCore.compileInsert(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
                .bindNull(1, Type.SHORT)
                .bindNull(2, Type.INT)
                .bindNull(3, Type.LONG)
                .bindNull(4, Type.FLOAT)
                .bindNull(5, Type.DOUBLE)
                .bindNull(6, Type.STRING)
                .bindNull(7, Type.BLOB)
                .bindShort(8, 5)
                .bindInt(9, 6)
                .bindLong(10, 7)
                .bindFloat(11, 8F)
                .bindDouble(12, 9.0)
                .bindString(13, "test 10")
                .bindBlob(14, byteArrayOf(11))
                .safeCloseAfterExecution()

        // Make sure record got inserted
        assertThat(nativeQueryExecutor.getCount(TABLE_NAME)).isEqualTo(1)

        // Execute update using engine
        val numberOfRowsAffected = engineCore.compileUpdate(
                "UPDATE $TABLE_NAME SET " +
                        "$SHORT_COLUMN_NAME = ?, " +
                        "$INT_COLUMN_NAME = ?, " +
                        "$LONG_COLUMN_NAME = ?, " +
                        "$FLOAT_COLUMN_NAME = ?, " +
                        "$DOUBLE_COLUMN_NAME = ?, " +
                        "$STRING_COLUMN_NAME = ?, " +
                        "$BLOB_COLUMN_NAME = ?, " +
                        "$NULLABLE_SHORT_COLUMN_NAME = ?, " +
                        "$NULLABLE_INT_COLUMN_NAME = ?, " +
                        "$NULLABLE_LONG_COLUMN_NAME = ?, " +
                        "$NULLABLE_FLOAT_COLUMN_NAME = ?, " +
                        "$NULLABLE_DOUBLE_COLUMN_NAME = ?, " +
                        "$NULLABLE_STRING_COLUMN_NAME = ?, " +
                        "$NULLABLE_BLOB_COLUMN_NAME = ? " +
                        "WHERE " +
                        "$SHORT_COLUMN_NAME IS NULL AND " +
                        "$INT_COLUMN_NAME IS NULL AND " +
                        "$LONG_COLUMN_NAME IS NULL AND " +
                        "$FLOAT_COLUMN_NAME IS NULL AND " +
                        "$DOUBLE_COLUMN_NAME IS NULL AND " +
                        "$STRING_COLUMN_NAME IS NULL AND " +
                        "$BLOB_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_SHORT_COLUMN_NAME = ? AND " +
                        "$NULLABLE_INT_COLUMN_NAME = ? AND " +
                        "$NULLABLE_LONG_COLUMN_NAME = ? AND " +
                        "$NULLABLE_FLOAT_COLUMN_NAME = ? AND " +
                        "$NULLABLE_DOUBLE_COLUMN_NAME = ? AND " +
                        "$NULLABLE_STRING_COLUMN_NAME = ? AND " +
                        "$NULLABLE_BLOB_COLUMN_NAME = ?"
        )
                .bindShort(1, 12)
                .bindInt(2, 13)
                .bindLong(3, 14)
                .bindFloat(4, 15F)
                .bindDouble(5, 16.0)
                .bindString(6, "test 17")
                .bindBlob(7, byteArrayOf(18))
                .bindNull(8, Type.SHORT)
                .bindNull(9, Type.INT)
                .bindNull(10, Type.LONG)
                .bindNull(11, Type.FLOAT)
                .bindNull(12, Type.DOUBLE)
                .bindNull(13, Type.STRING)
                .bindNull(14, Type.BLOB)
                .bindShort(15, 5)
                .bindInt(16, 6)
                .bindLong(17, 7)
                .bindFloat(18, 8F)
                .bindDouble(19, 9.0)
                .bindString(20, "test 10")
                .bindBlob(21, byteArrayOf(11))
                .safeCloseAfterExecution()

        assertThat(numberOfRowsAffected).isEqualTo(1)
        assertOneRowWithAllTypesUsingNativeMethod(
                12, 13, 14, 15F, 16.0, "test 17", byteArrayOf(18),
                null, null, null, null, null, null, null
        )
    }
}