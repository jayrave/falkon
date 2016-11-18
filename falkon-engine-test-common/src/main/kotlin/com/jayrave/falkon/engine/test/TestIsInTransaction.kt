package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestIsInTransaction private constructor(private val engineCore: EngineCore) {

    fun performTestReturnsAppropriateFlag() {
        val tableName = "test"
        val columnName = "column_name_1"

        // Outside transaction
        assertThat(engineCore.isInTransaction()).isFalse()

        engineCore.executeInTransaction {
            assertThat(engineCore.isInTransaction()).isTrue()
            engineCore.compileSql(
                    "CREATE TABLE $tableName ($columnName INTEGER)"
            ).safeCloseAfterExecution()

            assertThat(engineCore.isInTransaction()).isTrue()
        }

        // Outside transaction
        assertThat(engineCore.isInTransaction()).isFalse()

        engineCore.executeInTransaction {
            assertThat(engineCore.isInTransaction()).isTrue()
            engineCore.compileInsert(
                    "INSERT INTO $tableName ($columnName) VALUES (1)"
            ).safeCloseAfterExecution()

            assertThat(engineCore.isInTransaction()).isTrue()
        }

        // Outside transaction
        assertThat(engineCore.isInTransaction()).isFalse()
    }


    companion object {
        fun performTestReturnsAppropriateFlag(engineCore: EngineCore) {
            TestIsInTransaction(engineCore).performTestReturnsAppropriateFlag()
        }
    }
}