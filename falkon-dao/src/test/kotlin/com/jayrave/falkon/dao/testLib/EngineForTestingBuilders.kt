package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import java.util.*

/**
 * All compile* methods build [CompiledStatementForTest] (and derivatives) with the
 * passed in rawSql string
 */
internal class EngineForTestingBuilders private constructor(
        private val insertProvider: (String, String) -> OneShotCompiledInsertForTest,
        private val updateProvider: (String, String) -> OneShotCompiledUpdateForTest,
        private val deleteProvider: (String, String) -> OneShotCompiledDeleteForTest,
        private val queryProvider: (Iterable<String>, String) -> OneShotCompiledQueryForTest) :
        Engine {

    val compiledInserts = ArrayList<OneShotCompiledInsertForTest>()
    val compiledUpdates = ArrayList<OneShotCompiledUpdateForTest>()
    val compiledDeletes = ArrayList<OneShotCompiledDeleteForTest>()
    val compiledQueries = ArrayList<OneShotCompiledQueryForTest>()

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


    override fun compileInsert(tableName: String, rawSql: String): CompiledInsert {
        val compiledInsert = insertProvider.invoke(tableName, rawSql)
        compiledInserts.add(compiledInsert)
        return compiledInsert
    }


    override fun compileUpdate(tableName: String, rawSql: String): CompiledUpdate {
        val compiledUpdate = updateProvider.invoke(tableName, rawSql)
        compiledUpdates.add(compiledUpdate)
        return compiledUpdate
    }


    override fun compileDelete(tableName: String, rawSql: String): CompiledDelete {
        val compiledDelete = deleteProvider.invoke(tableName, rawSql)
        compiledDeletes.add(compiledDelete)
        return compiledDelete
    }


    override fun compileQuery(tableNames: Iterable<String>, rawSql: String): CompiledQuery {
        val compiledQuery = queryProvider.invoke(tableNames, rawSql)
        compiledQueries.add(compiledQuery)
        return compiledQuery
    }


    override fun registerDbEventListener(dbEventListener: DbEventListener) {
        throw UnsupportedOperationException()
    }


    override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
        throw UnsupportedOperationException()
    }



    companion object {
        
        fun createWithOneShotStatements(
                insertProvider: (String, String) -> OneShotCompiledInsertForTest =
                { tableName, sql -> OneShotCompiledInsertForTest(tableName, sql) },
                
                updateProvider: (String, String) -> OneShotCompiledUpdateForTest =
                { tableName, sql -> OneShotCompiledUpdateForTest(tableName, sql) },
                
                deleteProvider: (String, String) -> OneShotCompiledDeleteForTest =
                { tableName, sql -> OneShotCompiledDeleteForTest(tableName, sql) },
                
                queryProvider: (Iterable<String>, String) -> OneShotCompiledQueryForTest =
                { tableNames, sql -> OneShotCompiledQueryForTest(tableNames, sql) }):

                EngineForTestingBuilders {

            return EngineForTestingBuilders(
                    insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}