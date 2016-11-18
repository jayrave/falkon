package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestCompileUpdateWithoutBoundArgs private constructor(
        private val engineCore: EngineCore, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Create table & insert stuff using native methods
        nativeSqlExecutor.execute("CREATE TABLE $tableName ($columnName INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (1)")

        // Update stuff using engine
        engineCore.compileUpdate(
                "UPDATE $tableName SET $columnName = 5 WHERE $columnName = 1"
        ).safeCloseAfterExecution()

        // Query using native methods & perform assertions
        val source = nativeQueryExecutor.execute("SELECT * FROM $tableName")
        assertThat(source.moveToFirst()).isTrue()
        assertThat(source.getInt(source.getColumnIndex(columnName))).isEqualTo(5)
        assertThat(source.moveToNext()).isFalse()
        source.close()
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileUpdateWithoutBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}