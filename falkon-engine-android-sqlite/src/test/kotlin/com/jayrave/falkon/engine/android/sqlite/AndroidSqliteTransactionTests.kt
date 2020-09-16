package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestNestingTransactions
import org.junit.Test
import org.robolectric.RuntimeEnvironment

class AndroidSqliteTransactionTests : RobolectricTestBaseClass() {

    private val engineCore = buildAndroidSqliteEngineCore(RuntimeEnvironment.application)

    @Test
    fun `#isInTransaction returns appropriate result`() {
        TestIsInTransaction(engineCore).`#isInTransaction returns appropriate result`()
    }


    @Test
    fun `nesting transactions throws`() {
        TestNestingTransactions(engineCore).`nesting transactions throws`()
    }
}