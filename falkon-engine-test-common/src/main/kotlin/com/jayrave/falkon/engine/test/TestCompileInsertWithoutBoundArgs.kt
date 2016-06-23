package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileInsertWithoutBoundArgs private constructor(
        private val engine: Engine, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        // Create table using native methods
        nativeSqlExecutor.execute("CREATE TABLE test (column_name_1 INTEGER)")

        // Insert stuff using engine
        engine.compileInsert("INSERT INTO test (column_name_1) VALUES (1)").execute()
        engine.compileInsert("INSERT INTO test (column_name_1) VALUES (2)").execute()

        // Query via native methods & perform assertions
        assertThat(nativeQueryExecutor.getCount("test")).isEqualTo(2)
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileInsertWithoutBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}