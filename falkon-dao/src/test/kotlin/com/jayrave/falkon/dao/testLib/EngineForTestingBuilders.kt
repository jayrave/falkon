package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import java.util.*

/**
 * All compile* methods build [OneShotCompiledStatementForTest] (and derivatives) with the
 * passed in rawSql string
 */
internal class EngineForTestingBuilders private constructor(
        private val insertProvider: (String) -> OneShotCompiledInsertForTest,
        private val updateProvider: (String) -> OneShotCompiledUpdateForTest,
        private val deleteProvider: (String) -> OneShotCompiledDeleteForTest,
        private val queryProvider: (String) -> OneShotCompiledQueryForTest) : Engine {

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


    override fun compileSql(rawSql: String): CompiledStatement<Unit> {
        throw UnsupportedOperationException()
    }


    override fun compileInsert(rawSql: String): CompiledInsert {
        val compiledInsert = insertProvider.invoke(rawSql)
        compiledInserts.add(compiledInsert)
        return compiledInsert
    }


    override fun compileUpdate(rawSql: String): CompiledUpdate {
        val compiledUpdate = updateProvider.invoke(rawSql)
        compiledUpdates.add(compiledUpdate)
        return compiledUpdate
    }


    override fun compileDelete(rawSql: String): CompiledDelete {
        val compiledDelete = deleteProvider.invoke(rawSql)
        compiledDeletes.add(compiledDelete)
        return compiledDelete
    }


    override fun compileQuery(rawSql: String): CompiledQuery {
        val compiledQuery = queryProvider.invoke(rawSql)
        compiledQueries.add(compiledQuery)
        return compiledQuery
    }



    companion object {
        
        fun createWithOneShotStatements(
                insertProvider: (String) -> OneShotCompiledInsertForTest =
                { OneShotCompiledInsertForTest(it) },
                
                updateProvider: (String) -> OneShotCompiledUpdateForTest =
                { OneShotCompiledUpdateForTest(it) },
                
                deleteProvider: (String) -> OneShotCompiledDeleteForTest =
                { OneShotCompiledDeleteForTest(it) },
                
                queryProvider: (String) -> OneShotCompiledQueryForTest =
                { OneShotCompiledQueryForTest(it) }): EngineForTestingBuilders {

            return EngineForTestingBuilders(
                    insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}