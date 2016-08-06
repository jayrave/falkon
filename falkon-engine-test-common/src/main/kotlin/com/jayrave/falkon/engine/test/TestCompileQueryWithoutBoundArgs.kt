package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat

class TestCompileQueryWithoutBoundArgs private constructor(
        private val engineCore: EngineCore, private val nativeSqlExecutor: NativeSqlExecutor) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Create table & insert stuff using native methods
        nativeSqlExecutor.execute("CREATE TABLE $tableName ($columnName INTEGER)")
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (1)")
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (2)")
        nativeSqlExecutor.execute("INSERT INTO $tableName ($columnName) VALUES (3)")

        // Query using engine
        val source = engineCore.compileQuery("SELECT * FROM test ORDER BY column_name_1").execute()

        // Assert source's result set
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex(columnName))).isEqualTo(1)
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex(columnName))).isEqualTo(2)
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex(columnName))).isEqualTo(3)
        assertThat(source.moveToNext()).isEqualTo(false)
    }


    companion object {
        fun performTestOn(engineCore: EngineCore, usingNativeSqlExecutor: NativeSqlExecutor) {
            TestCompileQueryWithoutBoundArgs(engineCore, usingNativeSqlExecutor).performTest()
        }
    }
}