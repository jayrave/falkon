package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat

class TestCompileDeleteWithoutBoundArgs private constructor(
        private val engineCore: EngineCore, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Create table & insert stuff using native methods
        nativeSqlExecutor.execute("CREATE TABLE $tableName ($columnName INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (1)")

        // Store count before performing delete
        val countBeforeDelete = nativeQueryExecutor.getCount(tableName)

        // Delete stuff using engine
        engineCore.compileDelete("DELETE FROM $tableName").execute()

        // Get count after delete & perform assertions
        val countAfterDelete = nativeQueryExecutor.getCount(tableName)
        assertThat(countBeforeDelete).isGreaterThan(0)
        assertThat(countAfterDelete).isEqualTo(0)
    }


    companion object {
        fun performTestOn(
                engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileDeleteWithoutBoundArgs(
                    engineCore, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}