package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestTransactionCommitsIfSuccessful
import com.jayrave.falkon.engine.test.TestTransactionRollsbackIfUnSuccessful
import org.junit.Test
import java.sql.SQLException

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


    @Test(expected = SQLException::class)
    fun testWorkingOnStatementCreatedInATransactionOutsideOfItThrows() {
        val compiledStatement = engine.executeInTransaction {
            engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").execute()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (1)")
        }

        compiledStatement.execute()
    }


    @Test(expected = SQLException::class)
    fun testWorkingOnSourceCreatedInATransactionOutsideOfItThrows() {
        val source = engine.executeInTransaction {
            engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").execute()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (1)").execute()
            engine.compileQuery("SELECT * FROM test").execute()
        }

        source.moveToFirst()
    }
}