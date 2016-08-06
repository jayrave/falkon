package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat

class TestTransactionCommitsIfSuccessful private constructor(private val engineCore: EngineCore) {

    fun performTest() {
        val tableName = "test"
        val columnName = "column_name_1"

        engineCore.executeInTransaction {
            engineCore.compileSql("CREATE TABLE $tableName ($columnName INTEGER)").execute()
            engineCore.compileInsert("INSERT INTO $tableName ($columnName) VALUES (1)").execute()
            engineCore.compileInsert("INSERT INTO $tableName ($columnName) VALUES (2)").execute()
            engineCore.compileDelete("DELETE FROM $tableName WHERE $columnName = 2").execute()
            engineCore.compileUpdate(
                    "UPDATE $tableName SET $columnName = 5 WHERE $columnName = 1"
            ).execute()
        }


        val source = engineCore.compileQuery("SELECT * FROM $tableName").execute()
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex(columnName))).isEqualTo(5)
        assertThat(source.moveToNext()).isEqualTo(false)
    }


    companion object {
        fun performTestOn(engineCore: EngineCore) {
            TestTransactionCommitsIfSuccessful(engineCore).performTest()
        }
    }
}