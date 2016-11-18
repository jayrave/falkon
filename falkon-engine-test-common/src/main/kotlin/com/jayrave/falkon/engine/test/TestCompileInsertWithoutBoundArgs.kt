package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestCompileInsertWithoutBoundArgs private constructor(
        private val engineCore: EngineCore, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Create table using native methods
        nativeSqlExecutor.execute("CREATE TABLE $tableName ($columnName INTEGER)")

        // Insert stuff using engine
        engineCore.compileInsert(
                "INSERT INTO $tableName ($columnName) VALUES (1)"
        ).safeCloseAfterExecution()

        engineCore.compileInsert(
                "INSERT INTO $tableName ($columnName) VALUES (2)"
        ).safeCloseAfterExecution()

        // Query via native methods & perform assertions
        assertThat(nativeQueryExecutor.getCount(tableName)).isEqualTo(2)
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileInsertWithoutBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}