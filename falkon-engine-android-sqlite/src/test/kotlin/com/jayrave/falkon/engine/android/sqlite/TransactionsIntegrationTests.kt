package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.TestTransactionCommitsIfSuccessful
import com.jayrave.falkon.engine.test.TestTransactionRollsbackIfUnSuccessful
import org.junit.Test

class TransactionsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testTransactionCommitsIfSuccessful() {
        TestTransactionCommitsIfSuccessful.performTestOn(engineCore)
    }


    @Test
    fun testTransactionRollsbackIfUnSuccessful() {
        TestTransactionRollsbackIfUnSuccessful.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }
}