package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileDeleteWithoutBoundArgs private constructor(
        private val engine: Engine, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        // Create table & insert stuff using native methods
        nativeSqlExecutor.execute("CREATE TABLE test (column_name_1 INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (1)")

        // Store count before performing delete
        val countBeforeDelete = nativeQueryExecutor.getCount("test")

        // Delete stuff using engine
        engine.compileDelete("DELETE FROM test").execute()

        // Get count after delete & perform assertions
        val countAfterDelete = nativeQueryExecutor.getCount("test")
        assertThat(countBeforeDelete).isGreaterThan(0)
        assertThat(countAfterDelete).isEqualTo(0)
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileDeleteWithoutBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}