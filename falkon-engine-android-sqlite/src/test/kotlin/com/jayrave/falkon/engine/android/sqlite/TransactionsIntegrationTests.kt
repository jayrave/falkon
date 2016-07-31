package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestTransactionCommitsIfSuccessful
import com.jayrave.falkon.engine.test.TestTransactionRollsbackIfUnSuccessful
import org.junit.Test

class TransactionsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testTransactionCommitsIfSuccessful() {
        TestTransactionCommitsIfSuccessful.performTestOn(engine)
    }


    @Test
    fun testTransactionRollsbackIfUnSuccessful() {
        TestTransactionRollsbackIfUnSuccessful.performTestOn(
                engine, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testIsInTransaction() {
        TestIsInTransaction.performTestOn(engine)
    }
}