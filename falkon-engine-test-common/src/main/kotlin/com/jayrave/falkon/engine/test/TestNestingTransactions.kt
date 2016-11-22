package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.EngineCore
import org.assertj.core.api.Assertions.assertThat
import java.sql.SQLException

class TestNestingTransactions(private val engineCore: EngineCore) {

    fun `nesting transactions throws`() {
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
}