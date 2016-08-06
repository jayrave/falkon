package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock

internal class EngineCoreForTestingEngine private constructor(
        private val sqlProvider: (String) -> CompiledSqlForTest,
        private val insertProvider: (String) -> CompiledInsertForTest,
        private val updateProvider: (String) -> CompiledUpdateForTest,
        private val deleteProvider: (String) -> CompiledDeleteForTest,
        private val queryProvider: (String) -> CompiledQueryForTest) :
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

    override fun compileInsert(rawSql: String): CompiledInsert {
        return insertProvider.invoke(rawSql)
    }

    override fun compileUpdate(rawSql: String): CompiledUpdate {
        return updateProvider.invoke(rawSql)
    }

    override fun compileDelete(rawSql: String): CompiledDelete {
        return deleteProvider.invoke(rawSql)
    }

    override fun compileQuery(rawSql: String): CompiledQuery {
        return queryProvider.invoke(rawSql)
    }


    companion object {
        fun createWithCompiledStatementsForTest(
                sqlProvider: (String) -> CompiledSqlForTest = { sql -> CompiledSqlForTest(sql) },

                insertProvider: (String) -> CompiledInsertForTest =
                { sql -> CompiledInsertForTest(sql, 0) },

                updateProvider: (String) -> CompiledUpdateForTest =
                { sql -> CompiledUpdateForTest(sql, 0) },

                deleteProvider: (String) -> CompiledDeleteForTest =
                { sql -> CompiledDeleteForTest(sql, 0) },

                queryProvider: (String) -> CompiledQueryForTest =
                { sql -> CompiledQueryForTest(sql, mock()) }):

                EngineCoreForTestingEngine {

            return EngineCoreForTestingEngine(
                    sqlProvider, insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}