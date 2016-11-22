package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.safeCloseAfterExecution
import com.jayrave.falkon.engine.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SqlWithBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun `compile sql with all types of bound args`() {
        TestCompileSqlWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile insert with all types of bound args`() {
        TestCompileInsertWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile update with all types of bound args`() {
        TestCompileUpdateWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile delete with all types of bound args`() {
        TestCompileDeleteWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile query with all types of bound args`() {
        TestCompileQueryWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile insert or replace with all types of bound args`() {
        class TestCompileInsertOrReplaceWithAllTypesOfBoundArgs :
                BaseClassForTestingSqlWithAllTypesOfBoundArgs(
                        sqlExecutorUsingDataSource, queryExecutorUsingDataSource) {

            fun `perform test`() {
                nativeSqlExecutor.execute(
                        "CREATE TABLE $TABLE_NAME (" +
                                "$SHORT_COLUMN_NAME SMALLINT PRIMARY KEY, " +
                                "$INT_COLUMN_NAME INTEGER, " +
                                "$LONG_COLUMN_NAME BIGINT, " +
                                "$FLOAT_COLUMN_NAME REAL, " +
                                "$DOUBLE_COLUMN_NAME DOUBLE, " +
                                "$STRING_COLUMN_NAME VARCHAR, " +
                                "$BLOB_COLUMN_NAME BLOB, " +
                                "$NULLABLE_SHORT_COLUMN_NAME SMALLINT, " +
                                "$NULLABLE_INT_COLUMN_NAME INTEGER, " +
                                "$NULLABLE_LONG_COLUMN_NAME BIGINT, " +
                                "$NULLABLE_FLOAT_COLUMN_NAME REAL, " +
                                "$NULLABLE_DOUBLE_COLUMN_NAME DOUBLE, " +
                                "$NULLABLE_STRING_COLUMN_NAME VARCHAR, " +
                                "$NULLABLE_BLOB_COLUMN_NAME BLOB" +
                                ")"
                )

                val sql = "INSERT OR REPLACE INTO $TABLE_NAME (" +
                        "$SHORT_COLUMN_NAME, " +
                        "$INT_COLUMN_NAME, " +
                        "$LONG_COLUMN_NAME, " +
                        "$FLOAT_COLUMN_NAME, " +
                        "$DOUBLE_COLUMN_NAME, " +
                        "$STRING_COLUMN_NAME, " +
                        "$BLOB_COLUMN_NAME, " +
                        "$NULLABLE_SHORT_COLUMN_NAME, " +
                        "$NULLABLE_INT_COLUMN_NAME, " +
                        "$NULLABLE_LONG_COLUMN_NAME, " +
                        "$NULLABLE_FLOAT_COLUMN_NAME, " +
                        "$NULLABLE_DOUBLE_COLUMN_NAME, " +
                        "$NULLABLE_STRING_COLUMN_NAME, " +
                        "$NULLABLE_BLOB_COLUMN_NAME) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

                // Insert or replace using engine
                val numberOfRowsAffected = engineCore
                        .compileInsertOrReplace(sql)
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

        TestCompileInsertOrReplaceWithAllTypesOfBoundArgs().`perform test`()
    }
}