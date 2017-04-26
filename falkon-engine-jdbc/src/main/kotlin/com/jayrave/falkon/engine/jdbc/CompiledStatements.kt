package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.Source
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Could be used for compiling & executing any valid SQL statement
 */
internal class UnitReturningCompiledStatement(sql: String, connectionManager: ConnectionManager) :
        BaseCompiledStatement<Unit>(sql, connectionManager),
        com.jayrave.falkon.engine.CompiledStatement<Unit> {

    override fun execute() {
        preparedStatement.execute()
    }

    override fun prepareStatement(connection: Connection): PreparedStatement {
        return preparedStatementForNonQuerySql(sql, connection)
    }
}



/**
 * For compiling & executing
 *
 *  - INSERT
 *  - UPDATE
 *  - DELETE
 *  - statement similar to insert or replace
 */
internal class IUD_CompiledStatement(sql: String, connectionManager: ConnectionManager) :
        BaseCompiledStatement<Int>(sql, connectionManager) {

    override fun execute(): Int {
        return preparedStatement.executeUpdate()
    }

    override fun prepareStatement(connection: Connection): PreparedStatement {
        return preparedStatementForNonQuerySql(sql, connection)
    }
}



/**
 * For compiling & executing SELECT statements
 */
internal class CompiledQuery(
        sql: String, connectionManager: ConnectionManager,
        private val makeResultSetScrollable: Boolean) :
        BaseCompiledStatement<Source>(sql, connectionManager) {

    override fun execute(): Source {
        return ResultSetBackedSource(preparedStatement.executeQuery())
    }

    override fun prepareStatement(connection: Connection): PreparedStatement {
        val resultSetType = when {
            makeResultSetScrollable -> ResultSet.TYPE_SCROLL_INSENSITIVE
            else -> ResultSet.TYPE_FORWARD_ONLY
        }

        return connection.prepareStatement(sql, resultSetType, ResultSet.CONCUR_READ_ONLY)
    }
}



private fun preparedStatementForNonQuerySql(sql: String, connection: Connection):
        PreparedStatement {

    return connection.prepareStatement(
            sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
    )
}