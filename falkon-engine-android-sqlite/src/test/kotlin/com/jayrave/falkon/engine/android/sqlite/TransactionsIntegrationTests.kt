package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.TestTransactionCommitsIfSuccessful
import com.jayrave.falkon.engine.test.TestTransactionRollsbackIfUnSuccessful
import org.junit.Test

class TransactionsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun `transaction commits if successful`() {
        TestTransactionCommitsIfSuccessful(engineCore).`perform test`()
    }


    @Test
    fun `transaction rollsback if unsuccessful`() {
        TestTransactionRollsbackIfUnSuccessful(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }
}