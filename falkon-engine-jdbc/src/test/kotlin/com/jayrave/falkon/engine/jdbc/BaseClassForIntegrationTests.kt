package com.jayrave.falkon.engine.jdbc

import org.assertj.core.api.Assertions.assertThat
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import javax.sql.DataSource

abstract class BaseClassForIntegrationTests {

    protected lateinit var dataSource: DataSource
    protected lateinit var engine: JdbcEngine

    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=0", "user", "pw")
        engine = JdbcEngine(dataSource)
    }


    @After
    fun tearDown() {
        // http://www.h2database.com/html/grammar.html#shutdown
        // http://www.h2database.com/html/features.html#in_memory_databases
        // By default h2 closes the database when all existing connections to it are closed.
        // This makes sure that we have a clean slate for every test
        dataSource.connection.prepareStatement("SHUTDOWN").execute()
    }


    protected fun executeStatementUsingDataSource(statement: String) {
        dataSource.connection.prepareStatement(statement).execute()
    }


    protected fun countNumberOfRowsUsingDataSource(tableName: String): Int {
        val resultSet = dataSource
                .connection
                .prepareStatement("SELECT COUNT(*) AS count FROM $tableName")
                .executeQuery()

        assertThat(resultSet.first()).isTrue()
        val count = resultSet.getInt("count")
        assertThat(resultSet.next()).isFalse()
        return count
    }


    protected fun assertCountUsingDataSource(tableName: String, expectedCount: Int) {
        assertThat(countNumberOfRowsUsingDataSource(tableName)).isEqualTo(expectedCount)
    }
}