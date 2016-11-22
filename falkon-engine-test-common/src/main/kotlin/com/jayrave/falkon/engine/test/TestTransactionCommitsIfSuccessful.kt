package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestTransactionCommitsIfSuccessful(private val engineCore: EngineCore) {

    fun `perform test`() {
        val tableName = "test"
        val columnName = "column_name_1"

        engineCore.executeInTransaction {
            engineCore.compileSql(
                    "CREATE TABLE $tableName ($columnName INTEGER)"
            ).safeCloseAfterExecution()

            engineCore.compileInsert(
                    "INSERT INTO $tableName ($columnName) VALUES (1)"
            ).safeCloseAfterExecution()

            engineCore.compileInsert(
                    "INSERT INTO $tableName ($columnName) VALUES (2)"
            ).safeCloseAfterExecution()

            engineCore.compileDelete(
                    "DELETE FROM $tableName WHERE $columnName = 2"
            ).safeCloseAfterExecution()

            engineCore.compileUpdate(
                    "UPDATE $tableName SET $columnName = 5 WHERE $columnName = 1"
            ).safeCloseAfterExecution()
        }

        val compiledQuery = engineCore.compileQuery("SELECT * FROM $tableName")
        val source = compiledQuery.execute()
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex(columnName))).isEqualTo(5)
        assertThat(source.moveToNext()).isEqualTo(false)
        source.close()
        compiledQuery.close()
    }
}