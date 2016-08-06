package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestTransactionCommitsIfSuccessful
import com.jayrave.falkon.engine.test.TestTransactionRollsbackIfUnSuccessful
import org.junit.Test
import java.sql.SQLException

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


    @Test
    fun testIsInTransaction() {
        TestIsInTransaction.performTestReturnsAppropriateFlag(engineCore)
    }


    @Test(expected = SQLException::class)
    fun testWorkingOnStatementCreatedInATransactionOutsideOfItThrows() {
        val compiledStatement = engineCore.executeInTransaction {
            val tableName = "test"
            val columnName = "column_name_1"

            engineCore.compileSql("CREATE TABLE $tableName ($columnName INTEGER)").execute()
            engineCore.compileInsert("INSERT INTO $tableName ($columnName) VALUES (1)")
        }

        compiledStatement.execute()
    }


    @Test(expected = SQLException::class)
    fun testWorkingOnSourceCreatedInATransactionOutsideOfItThrows() {
        val source = engineCore.executeInTransaction {
            val tableName = "test"
            val columnName = "column_name_1"

            engineCore.compileSql("CREATE TABLE $tableName ($columnName INTEGER)").execute()
            engineCore.compileInsert("INSERT INTO $tableName ($columnName) VALUES (1)").execute()
            engineCore.compileQuery("SELECT * FROM $tableName").execute()
        }

        source.moveToFirst()
    }
}