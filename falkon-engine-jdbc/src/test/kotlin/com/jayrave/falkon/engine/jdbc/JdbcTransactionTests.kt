package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestNestingTransactions
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.Test

class JdbcTransactionTests {

    private val engineCore: JdbcEngineCore
    init {
        val dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test", "user", "pw")
        engineCore = JdbcEngineCore(dataSource)
    }

    @Test
    fun testIsInTransactionReturnsAppropriateFlag() {
        TestIsInTransaction.performTestReturnsAppropriateFlag(engineCore)
    }


    @Test
    fun testNestingTransactionThrows() {
        TestNestingTransactions.performTestNestingTransactionsThrows(engineCore)
    }
}