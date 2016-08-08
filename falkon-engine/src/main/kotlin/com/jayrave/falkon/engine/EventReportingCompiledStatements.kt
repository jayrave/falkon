package com.jayrave.falkon.engine

/**
 * Executes [onExecuteWithEffects] if on execute at least one row is inserted
 */
internal class EventReportingCompiledInsert(
        private val tableName: String, private val delegate: CompiledInsert,
        private val onExecuteWithEffects: (DbEvent) -> Unit) :
        CompiledInsert by delegate {

    override fun execute(): Int {
        val numberOfRowsInserted = delegate.execute()
        if (numberOfRowsInserted > 0) {
            onExecuteWithEffects.invoke(DbEvent.forInsert(tableName))
        }

        return numberOfRowsInserted
    }
}


/**
 * Executes [onExecuteWithEffects] if on execute at least one row is updated
 */
internal class EventReportingCompiledUpdate(
        private val tableName: String, private val delegate: CompiledUpdate,
        private val onExecuteWithEffects: (DbEvent) -> Unit) :
        CompiledUpdate by delegate {

    override fun execute(): Int {
        val numberOfRowsUpdated = delegate.execute()
        if (numberOfRowsUpdated > 0) {
            onExecuteWithEffects.invoke(DbEvent.forUpdate(tableName))
        }

        return numberOfRowsUpdated
    }
}


/**
 * Executes [onExecuteWithEffects] if on execute at least one row is deleted
 */
internal class EventReportingCompiledDelete(
        private val tableName: String, private val delegate: CompiledDelete,
        private val onExecuteWithEffects: (DbEvent) -> Unit) :
        CompiledDelete by delegate {

    override fun execute(): Int {
        val numberOfRowsDeleted = delegate.execute()
        if (numberOfRowsDeleted > 0) {
            onExecuteWithEffects.invoke(DbEvent.forDelete(tableName))
        }

        return numberOfRowsDeleted
    }
}