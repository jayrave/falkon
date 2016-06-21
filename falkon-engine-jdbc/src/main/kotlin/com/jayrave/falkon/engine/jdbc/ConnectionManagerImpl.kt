package com.jayrave.falkon.engine.jdbc

import java.sql.Connection
import javax.sql.DataSource

internal class ConnectionManagerImpl(
        private val dataSource: DataSource,
        private val transactionManager: TransactionManager) : ConnectionManager {

    override fun acquireConnection(): Connection {
        // If a transaction is going on, return that connection from transaction manager.
        // Otherwise get one from data source
        return transactionManager.getConnectionIfInTransaction() ?: dataSource.connection
    }

    override fun releaseConnection(connection: Connection) {
        // Connection should be closed explicitly only if it doesn't belong to a transaction.
        // If it does, it is managed by the transaction
        if (!transactionManager.belongsToTransaction(connection)) {
            connection.close()
        }
    }
}