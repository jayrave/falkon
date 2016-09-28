package com.jayrave.falkon.engine.jdbc

import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * A class to manage database transactions (nested transactions are not supported).
 * Exception is thrown when trying to nest transactions
 */
internal class TransactionManagerImpl(private val dataSource: DataSource) : TransactionManager {

    // Use ConnectionWrapperForTransaction to prevent unauthorized modification of connections
    // belonging to transactions
    private val connectionsForTransactions = ThreadLocal<ConnectionWrapperForTransaction?>()

    override fun <R> executeInTransaction(operation: () -> R): R {
        if (isInTransaction()) {
            throw SQLException("Transactions can't be nested")
        }

        // Start a new connection for this transaction
        connectionsForTransactions.set(ConnectionWrapperForTransaction(dataSource.connection))
        val connection = connectionsForTransactions.get()!!

        // Switch off auto commit => this is a transaction
        connection.delegate.autoCommit = false

        try {
            // Execute the desired operation & commit connection if successful
            val result = operation.invoke()
            connection.delegate.commit()
            return result

        } catch (e: Exception) {
            // An exception got thrown!! Rollback transaction & rethrow exception
            connection.delegate.rollback()
            throw e

        } finally {
            // Transaction has come to an end. Remove the stored connection
            connectionsForTransactions.remove()

            // Reset auto commit flag, close connection & remove it from the list
            connection.delegate.autoCommit = true
            connection.delegate.close()
        }
    }


    override fun isInTransaction(): Boolean {
        return getConnectionIfInTransaction() != null
    }


    override fun getConnectionIfInTransaction(): Connection? {
        return connectionsForTransactions.get()
    }


    override fun belongsToTransaction(connection: Connection): Boolean {
        return connectionsForTransactions.get() == connection
    }
}