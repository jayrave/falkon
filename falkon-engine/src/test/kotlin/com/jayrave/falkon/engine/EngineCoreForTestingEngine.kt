package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock

internal class EngineCoreForTestingEngine private constructor(
        private val sqlProvider: (String) -> CompiledSqlForTest,
        private val insertProvider: (String) -> CompiledStatementForInsertForTest,
        private val updateProvider: (String) -> CompiledStatementForUpdateForTest,
        private val deleteProvider: (String) -> CompiledStatementForDeleteForTest,
        private val queryProvider: (String) -> CompiledStatementForQueryForTest) :
        EngineCore {

    private var isInTransaction: Boolean = false
    var numberOfTransactionsReceived = 0
        private set

    var numberOfTransactionsCommitted = 0
        private set

    var numberOfTransactionsRolledBack = 0
        private set

    override fun <R> executeInTransaction(operation: () -> R): R {
        if (isInTransaction) {
            throw UnsupportedOperationException("Nested transactions not supported")
        }

        numberOfTransactionsReceived++
        return try {
            isInTransaction = true
            val result = operation.invoke()
            numberOfTransactionsCommitted++
            result

        } finally {
            isInTransaction = false
            numberOfTransactionsRolledBack++
        }
    }

    override fun isInTransaction(): Boolean {
        return isInTransaction
    }

    override fun compileSql(rawSql: String): CompiledStatement<Unit> {
        return sqlProvider.invoke(rawSql)
    }

    override fun compileInsert(rawSql: String): CompiledStatement<Int> {
        return insertProvider.invoke(rawSql)
    }

    override fun compileUpdate(rawSql: String): CompiledStatement<Int> {
        return updateProvider.invoke(rawSql)
    }

    override fun compileDelete(rawSql: String): CompiledStatement<Int> {
        return deleteProvider.invoke(rawSql)
    }

    override fun compileQuery(rawSql: String): CompiledStatement<Source> {
        return queryProvider.invoke(rawSql)
    }


    companion object {
        fun createWithCompiledStatementsForTest(
                sqlProvider: (String) -> CompiledSqlForTest = { sql -> CompiledSqlForTest(sql) },

                insertProvider: (String) -> CompiledStatementForInsertForTest =
                { sql -> CompiledStatementForInsertForTest(sql, 0) },

                updateProvider: (String) -> CompiledStatementForUpdateForTest =
                { sql -> CompiledStatementForUpdateForTest(sql, 0) },

                deleteProvider: (String) -> CompiledStatementForDeleteForTest =
                { sql -> CompiledStatementForDeleteForTest(sql, 0) },

                queryProvider: (String) -> CompiledStatementForQueryForTest =
                { sql -> CompiledStatementForQueryForTest(sql, mock()) }):

                EngineCoreForTestingEngine {

            return EngineCoreForTestingEngine(
                    sqlProvider, insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}