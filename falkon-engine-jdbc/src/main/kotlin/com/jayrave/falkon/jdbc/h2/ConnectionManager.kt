package com.jayrave.falkon.jdbc.h2

import java.sql.Connection
import javax.sql.DataSource

/**
 * A convenience class that manages acquiring & releasing [Connection]s. When using this class,
 * any connection acquired from this class should not be explicitly closed. It should just
 * be passed back to [releaseConnection] when done.
 *
 * *Note:* This class is transaction aware
 */
internal class ConnectionManager(
        private val dataSource: DataSource,
        private val transactionManager: TransactionManager) {

    fun acquireConnection(): Connection {
        // If a transaction is going on, return that connection from transaction manager.
        // Otherwise get one from data source
        return transactionManager.getConnectionIfInTransaction() ?: dataSource.connection
    }

    fun releaseConnection(connection: Connection) {
        // Connection should be closed explicitly only if it doesn't belong to a transaction.
        // If it does, it is managed by the transaction
        if (!transactionManager.belongsToTransaction(connection)) {
            connection.close()
        }
    }
}