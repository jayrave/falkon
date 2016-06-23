package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileSqlWithoutBoundArgs private constructor(
        private val engine: Engine, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        // Execute using engine
        engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").execute()

        // Insert using native method
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (1)")

        // Assert one row got inserted. Row would have been inserted only if the
        // CREATE statement had successfully executed
        assertThat(nativeQueryExecutor.getCount("test")).isEqualTo(1)
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileSqlWithoutBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}