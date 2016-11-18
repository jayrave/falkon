package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestCompileSqlWithoutBoundArgs private constructor(
        private val engineCore: EngineCore, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Execute using engine
        engineCore.compileSql(
                "CREATE TABLE $tableName ($columnName INTEGER)"
        ).safeCloseAfterExecution()

        // Insert using native method
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (1)")

        // Assert one row got inserted. Row would have been inserted only if the
        // CREATE statement had successfully executed
        assertThat(nativeQueryExecutor.getCount(tableName)).isEqualTo(1)
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileSqlWithoutBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}