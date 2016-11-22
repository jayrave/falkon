package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock

internal class EngineCoreForTestingEngine private constructor(
        private val sqlProvider: (String) -> CompiledStatement<Unit>,
        private val insertProvider: (String) -> CompiledStatement<Int>,
        private val updateProvider: (String) -> CompiledStatement<Int>,
        private val deleteProvider: (String) -> CompiledStatement<Int>,
        private val insertOrReplaceProvider: (String) -> CompiledStatement<Int>,
        private val queryProvider: (String) -> CompiledStatement<Source>) :
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

    override fun compileInsertOrReplace(rawSql: String): CompiledStatement<Int> {
        return insertOrReplaceProvider.invoke(rawSql)
    }

    override fun compileQuery(rawSql: String): CompiledStatement<Source> {
        return queryProvider.invoke(rawSql)
    }


    companion object {
        fun createWithCompiledStatementsForTest(
                sqlProvider: (String) -> CompiledStatement<Unit> =
                { sql -> UnitReturningCompiledStatementForTest(sql) },

                insertProvider: (String) -> CompiledStatement<Int> =
                { sql -> IntReturningCompiledStatementForTest(sql, 0) },

                updateProvider: (String) -> CompiledStatement<Int> =
                { sql -> IntReturningCompiledStatementForTest(sql, 0) },

                deleteProvider: (String) -> CompiledStatement<Int> =
                { sql -> IntReturningCompiledStatementForTest(sql, 0) },

                insertOrReplaceProvider: (String) -> CompiledStatement<Int> =
                { sql -> IntReturningCompiledStatementForTest(sql, 0) },

                queryProvider: (String) -> CompiledStatement<Source> =
                { sql -> CompiledStatementForQueryForTest(sql, mock()) }):

                EngineCoreForTestingEngine {

            return EngineCoreForTestingEngine(
                    sqlProvider, insertProvider, insertOrReplaceProvider,
                    updateProvider, deleteProvider, queryProvider
            )
        }
    }
}