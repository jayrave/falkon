package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import java.util.*

/**
 * Keep this class OPEN to allow spying (via Mockito)
 */
internal open class EngineForTestingBuilders private constructor(
        private val insertProvider: () -> OneShotCompiledInsertForTest,
        private val updateProvider: () -> OneShotCompiledUpdateForTest,
        private val deleteProvider: () -> OneShotCompiledDeleteForTest,
        private val queryProvider: () -> OneShotCompiledQueryForTest) : Engine {

    val compiledInserts = ArrayList<OneShotCompiledInsertForTest>()
    val compiledUpdates = ArrayList<OneShotCompiledUpdateForTest>()
    val compiledDeletes = ArrayList<OneShotCompiledDeleteForTest>()
    val compiledQueries = ArrayList<OneShotCompiledQueryForTest>()

    override fun <R> executeInTransaction(operation: () -> R): R? {
        return operation.invoke()
    }

    override fun compileInsert(tableName: String, columns: Iterable<String>): CompiledInsert {
        val compiledInsert = insertProvider.invoke()
        compiledInserts.add(compiledInsert)
        return compiledInsert
    }

    override fun compileUpdate(
            tableName: String, columns: Iterable<String>, whereClause: String?): CompiledUpdate {

        val compiledUpdate = updateProvider.invoke()
        compiledUpdates.add(compiledUpdate)
        return compiledUpdate
    }

    override fun compileDelete(tableName: String, whereClause: String?): CompiledDelete {
        val compiledDelete = deleteProvider.invoke()
        compiledDeletes.add(compiledDelete)
        return compiledDelete
    }

    override fun compileQuery(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereClause: String?, groupBy: Iterable<String>?, having: String?,
            orderBy: Iterable<Pair<String, Boolean>>?, limit: Long?, offset: Long?):
            CompiledQuery {

        val compiledQuery = queryProvider.invoke()
        compiledQueries.add(compiledQuery)
        return compiledQuery
    }

    override fun compileSql(rawSql: String): CompiledStatement<Unit> {
        throw UnsupportedOperationException()
    }

    override fun compileInsert(rawSql: String): CompiledInsert {
        throw UnsupportedOperationException()
    }

    override fun compileUpdate(rawSql: String): CompiledUpdate {
        throw UnsupportedOperationException()
    }

    override fun compileDelete(rawSql: String): CompiledDelete {
        throw UnsupportedOperationException()
    }

    override fun compileQuery(rawSql: String): CompiledQuery {
        throw UnsupportedOperationException()
    }


    companion object {
        
        fun createWithOneShotStatements(
                insertProvider: () -> OneShotCompiledInsertForTest =
                { OneShotCompiledInsertForTest() },
                
                updateProvider: () -> OneShotCompiledUpdateForTest =
                { OneShotCompiledUpdateForTest() },
                
                deleteProvider: () -> OneShotCompiledDeleteForTest =
                { OneShotCompiledDeleteForTest() },
                
                queryProvider: () -> OneShotCompiledQueryForTest =
                { OneShotCompiledQueryForTest() }): EngineForTestingBuilders {

            return EngineForTestingBuilders(
                    insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}