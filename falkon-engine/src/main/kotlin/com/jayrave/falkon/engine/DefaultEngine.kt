package com.jayrave.falkon.engine

class DefaultEngine(private val engineCore: EngineCore) : Engine {

    override fun <R> executeInTransaction(operation: () -> R): R {
        // If a transaction is already running, just execute this operation. This will make sure
        // that there is only one transaction at a time in a thread but provides the illusion that
        // transactions are nested. Doing this also holds up the required rules
        //
        //      - Only when the outermost transaction is committed, the changes will be persisted
        //      - If any of the transactions fail, the database will be restored to the state that
        //      was before the outermost transaction started
        //

        return when (isInTransaction()) {
            true -> operation.invoke()
            else -> engineCore.executeInTransaction(operation)
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
        return engineCore.compileInsert(rawSql)
    }


    override fun compileUpdate(tableName: String, rawSql: String): CompiledUpdate {
        return engineCore.compileUpdate(rawSql)
    }


    override fun compileDelete(tableName: String, rawSql: String): CompiledDelete {
        return engineCore.compileDelete(rawSql)
    }


    override fun compileQuery(tableNames: Iterable<String>, rawSql: String): CompiledQuery {
        return engineCore.compileQuery(rawSql)
    }
}