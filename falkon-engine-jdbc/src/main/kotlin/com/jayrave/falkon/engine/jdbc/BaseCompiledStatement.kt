package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Type
import java.io.ByteArrayInputStream
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Types

internal abstract class BaseCompiledStatement<T>(
        override val sql: String, private val connectionManager: ConnectionManager) :
        CompiledStatement<T> {

    private val connection: Connection = connectionManager.acquireConnection()
    protected val preparedStatement: PreparedStatement by lazy { prepareStatement(connection) }
    override final var isClosed = false
        private set

    override final fun bindShort(index: Int, value: Short): CompiledStatement<T> {
        preparedStatement.setShort(index, value)
        return this
    }

    override final fun bindInt(index: Int, value: Int): CompiledStatement<T> {
        preparedStatement.setInt(index, value)
        return this
    }

    override final fun bindLong(index: Int, value: Long): CompiledStatement<T> {
        preparedStatement.setLong(index, value)
        return this
    }

    override final fun bindFloat(index: Int, value: Float): CompiledStatement<T> {
        preparedStatement.setFloat(index, value)
        return this
    }

    override final fun bindDouble(index: Int, value: Double): CompiledStatement<T> {
        preparedStatement.setDouble(index, value)
        return this
    }

    override final fun bindString(index: Int, value: String): CompiledStatement<T> {
        preparedStatement.setString(index, value)
        return this
    }

    override final fun bindBlob(index: Int, value: ByteArray): CompiledStatement<T> {
        preparedStatement.setBlob(index, ByteArrayInputStream(value))
        return this
    }

    override final fun bindNull(index: Int, type: Type): CompiledStatement<T> {
        val sqlType = when (type) {
            Type.SHORT -> Types.SMALLINT
            Type.INT -> Types.INTEGER
            Type.LONG -> Types.BIGINT
            Type.FLOAT -> Types.REAL
            Type.DOUBLE -> Types.DOUBLE
            Type.STRING -> Types.VARCHAR
            Type.BLOB -> Types.BLOB
        }

        preparedStatement.setNull(index, sqlType)
        return this
    }

    override final fun close() {
        if (!isClosed) {
            var exceptionFromClosingStatement: Exception? = null
            var exceptionFromReleasingConnection: Exception? = null

            // Close prepare statement & capture the raised exception if any
            try {
                preparedStatement.close()
            } catch (e: Exception) {
                exceptionFromClosingStatement = e
            }

            // Release connection & capture the raised exception if any
            try {
                connectionManager.releaseConnection(connection)
            } catch (e: Exception) {
                exceptionFromReleasingConnection = e
            }

            // Rethrow the captured statement closing exception
            if (exceptionFromClosingStatement != null) {
                throw exceptionFromClosingStatement
            }

            // Rethrow the captured connection releasing exception
            if (exceptionFromReleasingConnection != null) {
                throw exceptionFromReleasingConnection
            }

            // Set flag
            isClosed = true
        }
    }

    override final fun clearBindings(): CompiledStatement<T> {
        preparedStatement.clearParameters()
        return this
    }

    /**
     * Implementations shouldn't call [preparedStatement] from this method
     */
    protected abstract fun prepareStatement(connection: Connection): PreparedStatement
}