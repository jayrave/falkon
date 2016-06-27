package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import com.nhaarman.mockito_kotlin.mock
import java.util.*

/**
 * All build* methods return a constant dummy string as SQL. All compile* methods return
 * mock statements (they are stored before returned so as to help verify interactions with it)
 */
internal class EngineForTestingDaoExtn private constructor(
        private val insertProvider: () -> CompiledInsert,
        private val updateProvider: () -> CompiledUpdate,
        private val deleteProvider: () -> CompiledDelete,
        private val queryProvider: () -> CompiledQuery) : Engine {

    val compiledInserts = ArrayList<CompiledInsert>()
    val compiledUpdates = ArrayList<CompiledUpdate>()
    val compiledDeletes = ArrayList<CompiledDelete>()
    val compiledQueries = ArrayList<CompiledQuery>()

    override fun <R> executeInTransaction(operation: () -> R): R {
        return operation.invoke()
    }


    override fun buildInsertSql(tableName: String, columns: Iterable<String>): String {
        return DUMMY_SQL
    }


    override fun buildUpdateSql(
            tableName: String, columns: Iterable<String>,
            whereSections: Iterable<WhereSection>?): String {

        return DUMMY_SQL
    }


    override fun buildDeleteSql(tableName: String, whereSections: Iterable<WhereSection>?): String {
        return DUMMY_SQL
    }


    override fun buildQuerySql(
            tableName: String, distinct: Boolean, columns: Iterable<String>?,
            whereSections: Iterable<WhereSection>?, groupBy: Iterable<String>?,
            orderBy: Iterable<OrderInfo>?, limit: Long?, offset: Long?): String {

        return DUMMY_SQL
    }


    override fun compileSql(rawSql: String): CompiledStatement<Unit> {
        throw UnsupportedOperationException()
    }


    override fun compileInsert(rawSql: String): CompiledInsert {
        compiledInserts.add(insertProvider.invoke())
        return compiledInserts.last()
    }


    override fun compileUpdate(rawSql: String): CompiledUpdate {
        compiledUpdates.add(updateProvider.invoke())
        return compiledUpdates.last()
    }


    override fun compileDelete(rawSql: String): CompiledDelete {
        compiledDeletes.add(deleteProvider.invoke())
        return compiledDeletes.last()
    }


    override fun compileQuery(rawSql: String): CompiledQuery {
        compiledQueries.add(queryProvider.invoke())
        return compiledQueries.last()
    }


    companion object {
        private const val DUMMY_SQL = "dummy sql"

        fun createWithMockStatements(
        insertProvider: () -> CompiledInsert = { mock<CompiledInsert>() },
        updateProvider: () -> CompiledUpdate = { mock<CompiledUpdate>() },
        deleteProvider: () -> CompiledDelete = { mock<CompiledDelete>() },
        queryProvider: () -> CompiledQuery = { mock<CompiledQuery>() }): EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn(
                    insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}