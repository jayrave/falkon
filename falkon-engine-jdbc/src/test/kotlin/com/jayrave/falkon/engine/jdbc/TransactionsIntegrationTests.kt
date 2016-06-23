package com.jayrave.falkon.engine.jdbc

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException

class TransactionsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testTransactionCommitsIfSuccessful() {
        engine.executeInTransaction {
            engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").execute()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (1)").execute()
            engine.compileInsert("INSERT INTO test (column_name_1) VALUES (2)").execute()
            engine
                    .compileUpdate("UPDATE test SET column_name_1 = 5 WHERE column_name_1 = 1")
                    .execute()

            engine.compileDelete("DELETE FROM test WHERE column_name_1 = 2").execute()
        }


        val source = engine.compileQuery("SELECT * FROM test").execute()
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(5)
        assertThat(source.moveToNext()).isEqualTo(false)
    }


    @Test
    fun testTransactionRollsbackIfUnSuccessful() {
        // Create table & insert stuff using data source
        executeStatementUsingDataSource("CREATE TABLE test (column_name_1 INTEGER)")
        executeStatementUsingDataSource("INSERT INTO test (column_name_1) VALUES (1)")

        val wasExceptionThrown: Boolean
        try {
            engine.executeInTransaction {
                engine.compileUpdate("DELETE FROM test").execute()
                throw RuntimeException()
            }

        } catch (e: Exception) {
            wasExceptionThrown = true
        }

        assertThat(wasExceptionThrown).isTrue()
        assertCountUsingDataSource("test", 1)
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