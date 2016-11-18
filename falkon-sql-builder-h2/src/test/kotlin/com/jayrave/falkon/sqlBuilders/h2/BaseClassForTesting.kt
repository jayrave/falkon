package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.test.DbForTest
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import javax.sql.DataSource

/**
 * Before every test, a clean database is setup
 */
abstract class BaseClassForTesting {

    private lateinit var dataSource: DataSource
    protected lateinit var db: DbForTest

    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        val ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "pw")
        ds.loginTimeout = 1
        ds.maxConnections = 1

        dataSource = ds
        db = object : DbForTest {
            override val intDataType: String = "INTEGER"
            override val stringDataType: String = "VARCHAR"
            override val dataSource: DataSource = this@BaseClassForTesting.dataSource
        }
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
}