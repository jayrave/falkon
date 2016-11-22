package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.*
import com.nhaarman.mockito_kotlin.mock
import java.util.*

/**
 * All build* methods return a constant dummy string as SQL. All compile* methods return
 * mock statements (they are stored before returned so as to help verify interactions with it)
 */
internal class EngineForTestingDaoExtn private constructor(
        private val insertProvider: () -> CompiledStatement<Int>,
        private val updateProvider: () -> CompiledStatement<Int>,
        private val deleteProvider: () -> CompiledStatement<Int>,
        private val queryProvider: () -> CompiledStatement<Source>) : Engine {

    val compiledStatementsForInsert = ArrayList<CompiledStatement<Int>>()
    val compiledStatementsForUpdate = ArrayList<CompiledStatement<Int>>()
    val compiledStatementsForDelete = ArrayList<CompiledStatement<Int>>()
    val compiledStatementsForQuery = ArrayList<CompiledStatement<Source>>()

    override fun <R> executeInTransaction(operation: () -> R): R {
        return operation.invoke()
    }


    override fun isInTransaction(): Boolean {
        throw UnsupportedOperationException()
    }


    override fun compileSql(tableNames: Iterable<String>?, rawSql: String):
            CompiledStatement<Unit> {

        throw UnsupportedOperationException()
    }


    override fun compileInsert(tableName: String, rawSql: String): CompiledStatement<Int> {
        compiledStatementsForInsert.add(insertProvider.invoke())
        return compiledStatementsForInsert.last()
    }


    override fun compileUpdate(tableName: String, rawSql: String): CompiledStatement<Int> {
        compiledStatementsForUpdate.add(updateProvider.invoke())
        return compiledStatementsForUpdate.last()
    }


    override fun compileDelete(tableName: String, rawSql: String): CompiledStatement<Int> {
        compiledStatementsForDelete.add(deleteProvider.invoke())
        return compiledStatementsForDelete.last()
    }


    override fun compileInsertOrReplace(tableName: String, rawSql: String): CompiledStatement<Int> {
        throw UnsupportedOperationException()
    }


    override fun compileQuery(tableNames: Iterable<String>, rawSql: String):
            CompiledStatement<Source> {

        compiledStatementsForQuery.add(queryProvider.invoke())
        return compiledStatementsForQuery.last()
    }


    override fun registerDbEventListener(dbEventListener: DbEventListener) {
        throw UnsupportedOperationException()
    }


    override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
        throw UnsupportedOperationException()
    }


    companion object {
        fun createWithMockStatements(
                insertProvider: () -> CompiledStatement<Int> = { mock<CompiledStatement<Int>>() },
                updateProvider: () -> CompiledStatement<Int> = { mock<CompiledStatement<Int>>() },
                deleteProvider: () -> CompiledStatement<Int> = { mock<CompiledStatement<Int>>() },
                queryProvider: () -> CompiledStatement<Source> =
                { mock<CompiledStatement<Source>>() }):
                EngineForTestingDaoExtn {

            return EngineForTestingDaoExtn(
                    insertProvider, updateProvider, deleteProvider, queryProvider
            )
        }
    }
}