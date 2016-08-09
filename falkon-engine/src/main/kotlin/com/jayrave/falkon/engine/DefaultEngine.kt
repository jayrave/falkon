package com.jayrave.falkon.engine

class DefaultEngine(private val engineCore: EngineCore) : Engine {

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

        return engineCore.compileSql(rawSql)
    }


    override fun compileInsert(tableName: String, rawSql: String): CompiledInsert {
        return EventReportingCompiledInsert(
                tableName, engineCore.compileInsert(rawSql), onExecuteWithEffects
        )
    }


    override fun compileUpdate(tableName: String, rawSql: String): CompiledUpdate {
        return EventReportingCompiledUpdate(
                tableName, engineCore.compileUpdate(rawSql), onExecuteWithEffects
        )
    }


    override fun compileDelete(tableName: String, rawSql: String): CompiledDelete {
        return EventReportingCompiledDelete(
                tableName, engineCore.compileDelete(rawSql), onExecuteWithEffects
        )
    }


    override fun compileQuery(tableNames: Iterable<String>, rawSql: String): CompiledQuery {
        return engineCore.compileQuery(rawSql)
    }


    override fun registerDbEventListener(dbEventListener: DbEventListener) {
        dbEventsManager.registerDbEventListener(dbEventListener)
    }


    override fun unregisterDbEventListener(dbEventListener: DbEventListener) {
        dbEventsManager.unregisterDbEventListener(dbEventListener)
    }
}