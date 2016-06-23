package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestTransactionRollsbackIfUnSuccessful private constructor(
        private val engine: Engine, private val nativeSqlExecutor: NativeSqlExecutor,
        private val nativeQueryExecutor: NativeQueryExecutor) {

    fun performTest() {
        // Create table & insert stuff using data source
        nativeSqlExecutor.execute("CREATE TABLE test (column_name_1 INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (1)")

        val wasExceptionThrown: Boolean
        try {
            engine.executeInTransaction {
                engine.compileUpdate("DELETE FROM test").execute()
                throw RuntimeException()
            }

        } catch (e: Exception) {
            wasExceptionThrown = true
        }

        assertThat(wasExceptionThrown).isTrue()
        assertThat(nativeQueryExecutor.getCount("test")).isEqualTo(1)
    }


    companion object {
        fun performTestOn(
                engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor,
                usingNativeQueryExecutor: NativeQueryExecutor) {

            TestTransactionRollsbackIfUnSuccessful(
                    engine, usingNativeSqlExecutor, usingNativeQueryExecutor
            ).performTest()
        }
    }
}