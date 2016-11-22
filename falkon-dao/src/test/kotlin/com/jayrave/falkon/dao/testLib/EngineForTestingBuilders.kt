package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import java.util.*

/**
 * All compile* methods build [CompiledStatementForTest] (and derivatives) with the
 * passed in rawSql string
 */
internal class EngineForTestingBuilders private constructor(
        private val insertProvider: (String, String) -> IntReturningOneShotCompiledStatementForTest,
        private val updateProvider: (String, String) -> IntReturningOneShotCompiledStatementForTest,
        private val deleteProvider: (String, String) -> IntReturningOneShotCompiledStatementForTest,
        private val insertOrReplaceProvider: (String, String) ->
        IntReturningOneShotCompiledStatementForTest,

        private val queryProvider: (Iterable<String>, String) ->
        OneShotCompiledStatementForQueryForTest) : Engine {

    val compiledStatementsForInsert = ArrayList<IntReturningOneShotCompiledStatementForTest>()
    val compiledStatementsForUpdate = ArrayList<IntReturningOneShotCompiledStatementForTest>()
    val compiledStatementsForDelete = ArrayList<IntReturningOneShotCompiledStatementForTest>()
    val compiledStatementsForQuery = ArrayList<OneShotCompiledStatementForQueryForTest>()
    val compiledStatementsForInsertOrReplace =
            ArrayList<IntReturningOneShotCompiledStatementForTest>()

    override fun <R> executeInTransaction(operation: () -> R): R {
        throw UnsupportedOperationException()
    }


    override fun isInTransaction(): Boolean {
        throw UnsupportedOperationException()
    }


    override fun compileSql(tableNames: Iterable<String>?, rawSql: String):
            CompiledStatement<Unit> {

        throw UnsupportedOperationException()
    }


    override fun compileInsert(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatementForInsert = insertProvider.invoke(tableName, rawSql)
        compiledStatementsForInsert.add(compiledStatementForInsert)
        return compiledStatementForInsert
    }


    override fun compileUpdate(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatementForUpdate = updateProvider.invoke(tableName, rawSql)
        compiledStatementsForUpdate.add(compiledStatementForUpdate)
        return compiledStatementForUpdate
    }


    override fun compileDelete(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatementForDelete = deleteProvider.invoke(tableName, rawSql)
        compiledStatementsForDelete.add(compiledStatementForDelete)
        return compiledStatementForDelete
    }


    override fun compileInsertOrReplace(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatementForInsertOrReplace = insertOrReplaceProvider.invoke(tableName, rawSql)
        compiledStatementsForInsertOrReplace.add(compiledStatementForInsertOrReplace)
        return compiledStatementForInsertOrReplace
    }


    override fun compileQuery(tableNames: Iterable<String>, rawSql: String):
            CompiledStatement<Source> {

        val compiledStatementForQuery = queryProvider.invoke(tableNames, rawSql)
        compiledStatementsForQuery.add(compiledStatementForQuery)
        return compiledStatementForQuery
    }


    override fun registerDbEventListener(dbEventListener: DbEventListener) {
        throw UnsupportedOperationException()
    }


    override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
        throw UnsupportedOperationException()
    }



    companion object {
        
        fun createWithOneShotStatements(
                insertProvider: (String, String) -> IntReturningOneShotCompiledStatementForTest =
                { tableName, sql -> IntReturningOneShotCompiledStatementForTest(tableName, sql) },
                
                updateProvider: (String, String) -> IntReturningOneShotCompiledStatementForTest =
                { tableName, sql -> IntReturningOneShotCompiledStatementForTest(tableName, sql) },
                
                deleteProvider: (String, String) -> IntReturningOneShotCompiledStatementForTest =
                { tableName, sql -> IntReturningOneShotCompiledStatementForTest(tableName, sql) },

                insertOrReplaceProvider: (String, String) ->
                IntReturningOneShotCompiledStatementForTest =
                { tableName, sql -> IntReturningOneShotCompiledStatementForTest(tableName, sql) },
                
                queryProvider: (Iterable<String>, String) ->
                OneShotCompiledStatementForQueryForTest =
                { tableNames, sql -> OneShotCompiledStatementForQueryForTest(tableNames, sql) }):

                EngineForTestingBuilders {

            return EngineForTestingBuilders(
                    insertProvider, updateProvider, deleteProvider,
                    insertOrReplaceProvider, queryProvider
            )
        }
    }
}