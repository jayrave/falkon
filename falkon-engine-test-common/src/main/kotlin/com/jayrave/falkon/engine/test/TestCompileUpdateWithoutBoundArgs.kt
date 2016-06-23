package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileUpdateWithoutBoundArgs private constructor(
        private val engine: Engine, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        // Create table & insert stuff using native methods
        nativeSqlExecutor.execute("CREATE TABLE test (column_name_1 INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (1)")

        // Update stuff using engine
        engine.compileUpdate("UPDATE test SET column_name_1 = 5 WHERE column_name_1 = 1").execute()

        // Query using native methods & perform assertions
        val source = nativeQueryExecutor.execute("SELECT * FROM test")
        assertThat(source.moveToFirst()).isTrue()
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(5)
        assertThat(source.moveToNext()).isFalse()
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestCompileUpdateWithoutBoundArgs(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}