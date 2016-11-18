package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.test.NativeQueryExecutor
import com.jayrave.falkon.engine.test.NativeSqlExecutor
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.After
import org.junit.Before
import java.sql.PreparedStatement
import javax.sql.DataSource

/**
 * Before every test, a clean database is setup
 */
abstract class BaseClassForIntegrationTests {

    protected lateinit var dataSource: DataSource
    protected lateinit var engineCore: JdbcEngineCore

    protected val sqlExecutorUsingDataSource = object : NativeSqlExecutor {
        override fun execute(sql: String) {
            dataSource.forStatement(sql) { it.execute() }
        }
    }

    protected val queryExecutorUsingDataSource = object : NativeQueryExecutor {
        override fun execute(query: String): Source {
            // Source closed by the caller
            val connection = dataSource.connection
            val resultSetBackedSource = ResultSetBackedSource(
                    connection.prepareStatement(query).executeQuery()
            )

            // Since connection isn't reused any where, close the connection when the result
            // set is closed
            return object : Source by resultSetBackedSource {
                override fun close() {
                    resultSetBackedSource.close()
                    connection.close()
                }
            }
        }
    }


    @Before
    fun setUp() {
        // http://www.h2database.com/html/features.html#in_memory_databases
        // Give the database a name to enabled multiple connections to the same database
        val ds = JdbcConnectionPool.create("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1", "user", "pw")
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
        dataSource.forStatement("SHUTDOWN") { it.execute() }
    }



    companion object {
        private fun <R> DataSource.forStatement(sql: String, op: (PreparedStatement) -> R): R {
            val connection = connection
            val preparedStatement = connection.prepareStatement(sql)
            return try {
                op.invoke(preparedStatement)
            } finally {
                preparedStatement.close()
                connection.close()
            }
        }
    }
}