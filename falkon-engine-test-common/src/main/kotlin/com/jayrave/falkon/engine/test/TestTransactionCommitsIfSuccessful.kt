package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestTransactionCommitsIfSuccessful private constructor(private val engine: Engine) {

    fun performTest() {
        engine.executeInTransaction {
            engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").execute()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (1)").execute()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (2)").execute()
            engine
                    .compileUpdate("UPDATE test SET column_name_1 = 5 WHERE column_name_1 = 1")
                    .execute()

            engine.compileDelete("DELETE FROM test WHERE column_name_1 = 2").execute()
        }


        val source = engine.compileQuery("SELECT * FROM test").execute()
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(5)
        assertThat(source.moveToNext()).isEqualTo(false)
    }


    companion object {
        fun performTestOn(engine: Engine) {
            TestTransactionCommitsIfSuccessful(engine).performTest()
        }
    }
}