package com.jayrave.falkon.engine

/**
 * Executes [onExecuteWithEffects] if [delegate.execute] affects at least one row
 */
internal class EventReportingCompiledStatement(
        private val tableName: String, private val eventType: DbEvent.Type,
        private val delegate: CompiledStatement<Int>,
        private val onExecuteWithEffects: (DbEvent) -> Unit) :
        CompiledStatement<Int> by delegate {

    override fun execute(): Int {
        val numberOfRowsAffected = delegate.execute()
        if (numberOfRowsAffected > 0) {
            onExecuteWithEffects.invoke(DbEvent.create(eventType, tableName))
        }

        return numberOfRowsAffected
    }
}