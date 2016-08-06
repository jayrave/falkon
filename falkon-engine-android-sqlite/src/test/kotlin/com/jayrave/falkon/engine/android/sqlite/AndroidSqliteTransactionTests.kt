package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestNestingTransactions
import org.junit.Test

class AndroidSqliteTransactionTests : RobolectricTestBaseClass() {

    private val engineCore = buildAndroidSqliteEngineCore()

    @Test
    fun testIsInTransactionReturnsAppropriateFlag() {
        TestIsInTransaction.performTestReturnsAppropriateFlag(engineCore)
    }


    @Test
    fun testNestingTransactionThrows() {
        TestNestingTransactions.performTestNestingTransactionsThrows(engineCore)
    }
}