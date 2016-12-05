package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestCompileQueryWithAllTypesOfBoundArgs(
        private val engineCore: EngineCore, nativeSqlExecutor: NativeSqlExecutor,
        nativeQueryExecutor: NativeQueryExecutor) :
        BaseClassForTestingSqlWithAllTypesOfBoundArgs(
                nativeSqlExecutor, nativeQueryExecutor) {

    fun `perform test`() {
        createTableWithColumnsForAllTypesUsingNativeMethod()

        // Execute insert using engine since its easier
        engineCore.compileInsert(getSqlToInsertOneRowWithAllTypesWithPlaceholders())
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

        // Make sure record got inserted
        assertThat(nativeQueryExecutor.getCount(TABLE_NAME)).isEqualTo(1)

        // Execute query using engine
        val compiledQuery = engineCore.compileQuery(
                "SELECT * FROM $TABLE_NAME WHERE " +
                        "$SHORT_COLUMN_NAME = ? AND " +
                        "$INT_COLUMN_NAME = ? AND " +
                        "$LONG_COLUMN_NAME = ? AND " +
                        "$FLOAT_COLUMN_NAME = ? AND " +
                        "$DOUBLE_COLUMN_NAME = ? AND " +
                        "$STRING_COLUMN_NAME = ? AND " +
                        "$BLOB_COLUMN_NAME = ? AND " +
                        "$NULLABLE_SHORT_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_INT_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_LONG_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_FLOAT_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_DOUBLE_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_STRING_COLUMN_NAME IS NULL AND " +
                        "$NULLABLE_BLOB_COLUMN_NAME IS NULL"
        )
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))

        val source = compiledQuery.execute()

        // Assert source's result set
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getShort(source.getColumnIndex(SHORT_COLUMN_NAME))).isEqualTo(5)
        assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(6)
        assertThat(source.getLong(source.getColumnIndex(LONG_COLUMN_NAME))).isEqualTo(7)
        assertThat(source.getFloat(source.getColumnIndex(FLOAT_COLUMN_NAME))).isEqualTo(8F)
        assertThat(source.getDouble(source.getColumnIndex(DOUBLE_COLUMN_NAME))).isEqualTo(9.0)
        assertThat(source.getString(source.getColumnIndex(STRING_COLUMN_NAME))).isEqualTo("test 10")
        assertThat(source.getBlob(source.getColumnIndex(BLOB_COLUMN_NAME))).isEqualTo(byteArrayOf(11))
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_SHORT_COLUMN_NAME))).isTrue()
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_INT_COLUMN_NAME))).isTrue()
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_LONG_COLUMN_NAME))).isTrue()
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_FLOAT_COLUMN_NAME))).isTrue()
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_DOUBLE_COLUMN_NAME))).isTrue()
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_STRING_COLUMN_NAME))).isTrue()
        assertThat(source.isNull(source.getColumnIndex(NULLABLE_BLOB_COLUMN_NAME))).isTrue()

        assertThat(source.moveToNext()).isEqualTo(false)
        source.close()
        compiledQuery.close()
    }
}