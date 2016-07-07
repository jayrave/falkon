package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.*
import javax.sql.DataSource

class JdbcEngine(dataSource: DataSource) : Engine {

    private val transactionManager = TransactionManagerImpl(dataSource)
    private val connectionManager = ConnectionManagerImpl(dataSource, transactionManager)

    /**
     * Database resources created inside a transaction shouldn't be passed outside as
     * the underlying database connection would be closed when the transaction completes
     * (which may lead to exceptions being thrown when the resources are acted upon).
     * Therefore, don't pass any [CompiledStatement] or [Source] (or any such resources)
     * outside of the transaction block
     */
    override fun <R> executeInTransaction(operation: () -> R): R {
        return transactionManager.executeInTransaction(operation)
    }


    override fun compileSql(rawSql: String): CompiledStatement<Unit> {
        return UnitReturningCompiledStatement(rawSql, connectionManager)
    }


    override fun compileInsert(rawSql: String): CompiledInsert {
        return IUD_CompiledStatement(rawSql, connectionManager)
    }


    override fun compileUpdate(rawSql: String): CompiledUpdate {
        return IUD_CompiledStatement(rawSql, connectionManager)
    }


    override fun compileDelete(rawSql: String): CompiledDelete {
        return IUD_CompiledStatement(rawSql, connectionManager)
    }


    override fun compileQuery(rawSql: String): com.jayrave.falkon.engine.CompiledQuery {
        return CompiledQuery(rawSql, connectionManager)
    }
}