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


    override fun buildInsertSql(tableName: String, columns: Iterable<String>): String {
        return SqlBuilderFromParts.buildInsertSqlOrThrow(tableName, columns)
    }


    override fun buildUpdateSql(
            tableName: String, columns: Iterable<String>,
            whereSections: Iterable<WhereSection>?): String {

        return SqlBuilderFromParts.buildUpdateSqlOrThrow(tableName, columns, whereSections)
    }


    override fun buildDeleteSql(
            tableName: String, whereSections: Iterable<WhereSection>?): String {

        return SqlBuilderFromParts.buildDeleteSql(tableName, whereSections)
    }


    override fun buildQuerySql(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?): String {

        return SqlBuilderFromParts.buildQuerySql(
                tableName, distinct, columns, whereSections, groupBy,
                orderBy, limit, offset
        )
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