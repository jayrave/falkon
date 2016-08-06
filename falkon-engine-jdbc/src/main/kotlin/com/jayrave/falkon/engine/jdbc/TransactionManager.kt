package com.jayrave.falkon.engine.jdbc

import java.sql.Connection

/**
 * Implementations must hold up these rules
 *
 *      - Only one transaction can be active per thread at a time
 *      - Transactions mustn't be shared across threads
 *      - Should throw on trying to nest transactions
 */
internal interface TransactionManager {

    /**
     * The passed in [operation] will be executed inside a transaction
     */
    fun <R> executeInTransaction(operation: () -> R): R?

    /**
     * @return `true` if there is an active transaction in the current thread
     */
    fun isInTransaction(): Boolean

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