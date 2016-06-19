package com.jayrave.falkon.engine.jdbc

import java.sql.Connection

/**
 * All implementations must also support nested transactions
 */
internal interface TransactionManager {

    /**
     * The passed in [operation] will be executed inside a transaction
     */
    fun <R> executeInTransaction(operation: () -> R): R?

    /**
     * Any [Connection] acquired via this method shouldn't be closed explicitly. When the
     * transaction gets over, connection will be closed automatically
     */
    fun getConnectionIfInTransaction(): Connection?

    /**
     * @return whether the passed in [connection] belongs in the transaction (if there is one)
     */
    fun belongsToTransaction(connection: Connection): Boolean
}