package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.test.TestIsInTransaction
import com.jayrave.falkon.engine.test.TestNestingTransactions
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.sql.DataSource

/**
 * Before every test, a clean database is setup
 */
class JdbcTransactionTests {

    private lateinit var dataSource: DataSource
    private lateinit var engineCore: JdbcEngineCore

    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        val ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "pw")
        ds.loginTimeout = 1
        ds.maxConnections = 1

        dataSource = ds
        engineCore = JdbcEngineCore(dataSource)
    }

    @After
    fun tearDown() {
        // http://www.h2database.com/html/grammar.html#shutdown
        // http://www.h2database.com/html/features.html#in_memory_databases
        // By default h2 closes the database when all existing connections to it are closed.
        // For an in-memory db, closing is akin to nuking it. This makes sure that we have a
        // clean slate for every test
        dataSource.connection.prepareStatement("SHUTDOWN").execute()
    }


    @Test
    fun `#isInTransaction returns appropriate result`() {
        TestIsInTransaction(engineCore).`#isInTransaction returns appropriate result`()
    }


    @Test
    fun `nesting transactions throws`() {
        TestNestingTransactions(engineCore).`nesting transactions throws`()
    }
}