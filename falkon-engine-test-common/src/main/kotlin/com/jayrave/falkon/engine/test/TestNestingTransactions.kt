package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat
import java.sql.SQLException

class TestNestingTransactions private constructor(private val engineCore: EngineCore) {

    fun performTestNestingTransactionsThrows() {
        var exceptionCaught = false
        engineCore.executeInTransaction {
            try {
                engineCore.executeInTransaction {}
            } catch (e: SQLException) {
                exceptionCaught = true
            }
        }

        assertThat(exceptionCaught).isTrue()
    }


    companion object {
        fun performTestNestingTransactionsThrows(engineCore: EngineCore) {
            TestNestingTransactions(engineCore).performTestNestingTransactionsThrows()
        }
    }
}