package com.jayrave.falkon.engine

/**
 * A default implementation of [Engine] that takes care of nesting transactions & also
 * informing [DbEventListener] about [DbEvent]s
 *
 * @param engineCore that does the actual compilation of the SQL statements
 * @param logger to pass the SQL & bound arguments on executing a [CompiledStatement].
 * **PLEASE DO NOT USE [Logger] in PRODUCTION**
 */
class DefaultEngine(
        private val engineCore: EngineCore, private val logger: Logger? = null) :
        Engine {

    private val dbEventsManager = DbEventsManager() { isInTransaction() }
    private val onExecuteWithEffects = { dbEvent: DbEvent -> dbEventsManager.onEvent(dbEvent) }

    override fun <R> executeInTransaction(operation: () -> R): R {
        // If a transaction is already running, just execute this operation. This will make sure
        // that there is only one transaction at a time in a thread but provides the illusion that
        // transactions are nested. Doing this also holds up the required rules
        //
        //      - Only when the outermost transaction is committed, the changes will be persisted
        //      - If any of the transactions fail, the database will be restored to the state that
        //      was before the outermost transaction started
        //

        try {
            val result = when (isInTransaction()) {
                true -> operation.invoke()
                else -> engineCore.executeInTransaction(operation)
            }

            // Let manager know that the transaction was successful
            dbEventsManager.onTransactionCommittedSuccessfully()
            return result

        } catch (e: Exception) {
            // Let manager know that the transaction was rolled back
            dbEventsManager.onTransactionRolledBack()
            throw e
        }
    }


    override fun isInTransaction(): Boolean {
        return engineCore.isInTransaction()
    }


    override fun compileSql(
            tableNames: Iterable<String>?, rawSql: String):
            CompiledStatement<Unit> {

        val compiledStatement = engineCore.compileSql(rawSql)
        return wrapForLoggingIfRequired(compiledStatement)
    }


    override fun compileInsert(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatement = EventReportingCompiledStatement(
                tableName, DbEvent.Type.INSERT, engineCore.compileInsert(rawSql),
                onExecuteWithEffects
        )

        return wrapForLoggingIfRequired(compiledStatement)
    }


    override fun compileUpdate(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatement = EventReportingCompiledStatement(
                tableName, DbEvent.Type.UPDATE, engineCore.compileUpdate(rawSql),
                onExecuteWithEffects
        )

        return wrapForLoggingIfRequired(compiledStatement)
    }


    override fun compileDelete(tableName: String, rawSql: String): CompiledStatement<Int> {
        val compiledStatement = EventReportingCompiledStatement(
                tableName, DbEvent.Type.DELETE, engineCore.compileDelete(rawSql),
                onExecuteWithEffects
        )

        return wrapForLoggingIfRequired(compiledStatement)
    }


    override fun compileQuery(
            tableNames: Iterable<String>, rawSql: String):
            CompiledStatement<Source> {

        val compiledStatement = engineCore.compileQuery(rawSql)
        return wrapForLoggingIfRequired(compiledStatement)
    }


    override fun registerDbEventListener(dbEventListener: DbEventListener) {
        dbEventsManager.registerDbEventListener(dbEventListener)
    }


    override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
        dbEventsManager.unregisterDbEventListener(dbEventListener)
    }


    private fun <R> wrapForLoggingIfRequired(compiledStatement: CompiledStatement<R>):
            CompiledStatement<R> {

        return when (logger) {
            null -> compiledStatement
            else -> return LoggingCompiledStatement(compiledStatement, logger)
        }
    }
}