package com.jayrave.falkon.jdbc.h2

import java.sql.Connection
import java.sql.Savepoint
import java.util.concurrent.Executor

/**
 * Prevents accidentally closing, committing, setting save points or rolling back the connection
 * by throwing an [UnsupportedOperationException]
 */
internal class ConnectionWrapperForTransaction(val delegate: Connection) : Connection by delegate {

    override fun abort(executor: Executor?) {
        throwException("abort")
    }

    override fun close() {
        throwException("close")
    }

    override fun commit() {
        throwException("commit")
    }

    override fun releaseSavepoint(savepoint: Savepoint?) {
        throwException("releaseSavepoint")
    }

    override fun rollback() {
        throwException("rollback")
    }

    override fun rollback(savepoint: Savepoint?) {
        throwException("rollback")
    }

    override fun setAutoCommit(autoCommit: Boolean) {
        throwException("setAutoCommit")
    }

    override fun setSavepoint(): Savepoint? {
        throwException("setSavepoint")
        return null
    }

    override fun setSavepoint(name: String?): Savepoint? {
        throwException("setSavepoint")
        return null
    }

    override fun setTransactionIsolation(level: Int) {
        throwException("setTransactionIsolation")
    }

    private fun throwException(methodName: String) {
        @Suppress("ConvertToStringTemplate")
        throw UnsupportedOperationException(
                "This connection is in a transaction and is therefore managed. " +
                        "Calling #$methodName is not supported"
        )
    }
}