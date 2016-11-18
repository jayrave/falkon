package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestTransactionRollsbackIfUnSuccessful private constructor(
        private val engineCore: EngineCore, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Create table & insert stuff using data source
        nativeSqlExecutor.execute("CREATE TABLE $tableName ($columnName INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (1)")

        val wasExceptionThrown: Boolean
        try {
            engineCore.executeInTransaction {
                engineCore.compileUpdate("DELETE FROM $tableName").safeCloseAfterExecution()
                throw RuntimeException()
            }

        } catch (e: Exception) {
            wasExceptionThrown = true
        }

        assertThat(wasExceptionThrown).isTrue()
        assertThat(nativeQueryExecutor.getCount(tableName)).isEqualTo(1)
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestTransactionRollsbackIfUnSuccessful(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}