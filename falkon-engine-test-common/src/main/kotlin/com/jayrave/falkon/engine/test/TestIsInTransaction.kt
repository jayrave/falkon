package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestIsInTransaction private constructor(private val engine: Engine) {

    fun performTest() {
        // Outside transaction
        assertThat(engine.isInTransaction()).isFalse()

        engine.executeInTransaction {
            assertThat(engine.isInTransaction()).isTrue()
            engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").execute()
            assertThat(engine.isInTransaction()).isTrue()
        }

        // Outside transaction
        assertThat(engine.isInTransaction()).isFalse()

        engine.executeInTransaction {
            assertThat(engine.isInTransaction()).isTrue()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (1)").execute()
            assertThat(engine.isInTransaction()).isTrue()
        }

        // Outside transaction
        assertThat(engine.isInTransaction()).isFalse()
    }


    // TODO - how to test #isInTransaction in parallel transactions


    companion object {
        fun performTestOn(engine: Engine) {
            TestIsInTransaction(engine).performTest()
        }
    }
}