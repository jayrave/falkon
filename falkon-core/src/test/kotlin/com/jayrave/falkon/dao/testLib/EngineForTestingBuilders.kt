package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import java.util.*

/**
 * All build* methods return dummy SQL built by stringifying and concatenating the parameters.
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

    override fun <R> executeInTransaction(operation: () -> R): R? {
        throw UnsupportedOperationException()
    }

    override fun buildInsertSql(tableName: String, columns: Iterable<String>): String {
        return buildDummyInsertSql(tableName, columns)
    }

    override fun buildUpdateSql(tableName: String, columns: Iterable<String>, whereClause: String?):
            String {
        return buildDummyUpdateSql(tableName, columns, whereClause)
    }

    override fun buildDeleteSql(tableName: String, whereClause: String?): String {
        return buildDummyDeleteSql(tableName, whereClause)
    }

    override fun buildQuerySql(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereClause: String?, groupBy: Iterable<String>?, having: String?,
            orderBy: Iterable<Pair<String, Boolean>>?, limit: Long?, offset: Long?): String {

        return buildDummyQuerySql(
                tableName, distinct, columns, whereClause, groupBy,
                having, orderBy, limit, offset
        )
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


        fun buildDummyInsertSql(tableName: String, columns: Iterable<String>): String {
            return "tableName: $tableName; columns: ${columns.joinToString()}"
        }

        fun buildDummyUpdateSql(tableName: String, columns: Iterable<String>, whereClause: String?):
                String {
            return "tableName: $tableName; columns: ${columns.joinToString()}; " +
                    "whereClause: $whereClause"
        }

        fun buildDummyDeleteSql(tableName: String, whereClause: String?): String {
            return "tableName: $tableName; whereClause: $whereClause"
        }

        fun buildDummyQuerySql(
                tableName: String, distinct: Boolean = false, columns: Iterable<String>? = null,
                whereClause: String? = null, groupBy: Iterable<String>? = null,
                having: String? = null, orderBy: Iterable<Pair<String, Boolean>>? = null,
                limit: Long? = null, offset: Long? = null): String {

            return "tableName: $tableName; distinct: $distinct; " +
                    "columns: ${columns?.joinToString()}; whereClause: $whereClause; " +
                    "groupBy: ${groupBy?.joinToString()}; having: $having; " +
                    "orderBy: ${orderBy?.joinToString()}; limit: $limit; offset: $offset"
        }
    }
}