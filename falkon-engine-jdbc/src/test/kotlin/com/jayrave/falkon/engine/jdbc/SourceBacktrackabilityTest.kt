package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.sql.DataSource

class SourceBacktrackabilityTest {

    private lateinit var dataSource: DataSource

    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        val ds = JdbcConnectionPool.create("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1", "user", "pw")
        ds.loginTimeout = 1
        ds.maxConnections = 1

        dataSource = ds
    }


    @After
    fun tearDown() {
        // http://www.h2database.com/html/grammar.html#shutdown
        // http://www.h2database.com/html/features.html#in_memory_databases
        // By default h2 closes the database when all existing connections to it are closed.
        // For an in-memory db, closing is akin to nuking it. This makes sure that we have a
        // clean slate for every test
        dataSource.forStatement("SHUTDOWN") { it.execute() }
    }


    @Test
    fun sourceThatCanBacktrack() {
        assertBacktrackability(JdbcEngineCore(dataSource, true), true)
    }


    @Test
    fun sourceThatCanNotBacktrack() {
        assertBacktrackability(JdbcEngineCore(dataSource, false), false)
    }


    private fun assertBacktrackability(engineCore: JdbcEngineCore, isBacktrackable: Boolean) {
        engineCore.compileSql("CREATE TABLE test (col_1 INTEGER)").safeCloseAfterExecution()
        engineCore.compileQuery("SELECT * FROM test").use { compiledQuery ->
            compiledQuery.execute().use { source ->
                assertThat(source.canBacktrack).isEqualTo(isBacktrackable)
            }
        }
    }
}