package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestTransactionCommitsIfSuccessful
import com.jayrave.falkon.engine.test.TestTransactionRollsbackIfUnSuccessful
import org.junit.Test
import java.sql.SQLException

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


    @Test
    fun `#isInTransaction returns appropriate result`() {
        TestIsInTransaction(engineCore).`#isInTransaction returns appropriate result`()
    }


    @Test(expected = SQLException::class)
    fun `working on statement after associated transaction is closed throws`() {
        val compiledStatement = engineCore.executeInTransaction {
            val tableName = "test"
            val columnName = "column_name_1"

            engineCore.compileSql("CREATE TABLE $tableName ($columnName INTEGER)").execute()
            engineCore.compileInsert("INSERT INTO $tableName ($columnName) VALUES (1)")
        }

        compiledStatement.execute()
    }


    @Test(expected = SQLException::class)
    fun `working on source after associated transaction is closed throws`() {
        val source = engineCore.executeInTransaction {
            val tableName = "test"
            val columnName = "column_name_1"

            engineCore.compileSql("CREATE TABLE $tableName ($columnName INTEGER)").execute()
            engineCore.compileInsert("INSERT INTO $tableName ($columnName) VALUES (1)").execute()
            engineCore.compileQuery("SELECT * FROM $tableName").execute()
        }

        source.moveToNext()
    }
}