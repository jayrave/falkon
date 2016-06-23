package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestCompileQueryWithoutBoundArgs private constructor(
        private val engine: Engine, private val nativeSqlExecutor: NativeSqlExecutor) {

    fun performTest() {
        // Create table & insert stuff using native methods
        nativeSqlExecutor.execute("CREATE TABLE test (column_name_1 INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (1)")
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (2)")
        nativeSqlExecutor.execute("INSERT INTO test (column_name_1) VALUES (3)")

        // Query using engine
        val source = engine.compileQuery("SELECT * FROM test ORDER BY column_name_1").execute()

        // Assert source's result set
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(1)
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(2)
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(3)
        assertThat(source.moveToNext()).isEqualTo(false)
    }


    companion object {
        fun performTestOn(engine: Engine, usingNativeSqlExecutor: NativeSqlExecutor) {
            TestCompileQueryWithoutBoundArgs(engine, usingNativeSqlExecutor).performTest()
        }
    }
}