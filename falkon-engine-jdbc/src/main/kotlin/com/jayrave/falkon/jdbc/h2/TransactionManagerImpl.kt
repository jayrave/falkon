package com.jayrave.falkon.jdbc.h2

import java.sql.Connection
import java.util.*
import javax.sql.DataSource

/**
 * A class to manage database transactions (even nested transactions are supported).
 * The dynamics between internal & parent transaction is dependent on the passed in
 * [DataSource]
 */
internal class TransactionManagerImpl(private val dataSource: DataSource) : TransactionManager {

    // Use ConnectionWrapperForTransaction to prevent unauthorized modification of connections
    // belonging to transactions
    private val connectionListsForTransactions =
            ThreadLocal<LinkedList<ConnectionWrapperForTransaction>?>()


    override fun <R> executeInTransaction(operation: () -> R): R? {
        val connectionList = getConnectionList()

        // Create new connection & add to list
        val connection = ConnectionWrapperForTransaction(dataSource.connection)
        connectionList.addLast(connection)

        // Create result reference
        var result: R? = null

        // Switch off auto commit => this is a transaction
        connection.delegate.autoCommit = false

        try {
            // Execute the desired operation & commit connection if successful
            result = operation.invoke()
            connection.delegate.commit()

        } catch (e: Exception) {
            // An exception got thrown!! Rollback transaction
            connection.delegate.rollback()

        } finally {
            // Reset auto commit flag, close connection & remove it from the list
            connection.delegate.autoCommit = true
            connection.delegate.close()
            connectionList.remove(connection)
        }

        return result
    }


    override fun getConnectionIfInTransaction(): Connection? {
        // Return the inner most connection
        return connectionListsForTransactions.get()?.last
    }


    override fun belongsToTransaction(connection: Connection): Boolean {
        return connectionListsForTransactions.get()?.contains(connection) ?: false
    }


    private fun getConnectionList(): LinkedList<ConnectionWrapperForTransaction> {
        // Create & set a list if one isn't present already
        var connectionList = connectionListsForTransactions.get()
        if (connectionList == null) {
            connectionListsForTransactions.set(LinkedList())
            connectionList = connectionListsForTransactions.get()!!
        }

        return connectionList
    }
}