package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.test.NativeQueryExecutor
import com.jayrave.falkon.engine.test.NativeSqlExecutor
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import javax.sql.DataSource

abstract class BaseClassForIntegrationTests {

    protected lateinit var dataSource: DataSource
    protected lateinit var engineCore: JdbcEngineCore

    protected val sqlExecutorUsingDataSource = object : NativeSqlExecutor {
        override fun execute(sql: String) {
            dataSource.connection.prepareStatement(sql).execute()
        }
    }

    protected val queryExecutorUsingDataSource = object : NativeQueryExecutor {
        override fun execute(query: String): Source {
            return ResultSetBackedSource(
                    dataSource.connection.prepareStatement(query).executeQuery()
            )
        }
    }


    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        dataSource = JdbcConnectionPool.create(
                "jdbc:h2:mem:db_for_tests;DB_CLOSE_DELAY=0", "user", "pw"
        )

        engineCore = JdbcEngineCore(dataSource)
    }


    @After
    fun tearDown() {
        // http://www.h2database.com/html/grammar.html#shutdown
        // http://www.h2database.com/html/features.html#in_memory_databases
        // By default h2 closes the database when all existing connections to it are closed.
        // This makes sure that we have a clean slate for every test
        dataSource.connection.prepareStatement("SHUTDOWN").execute()
    }
}