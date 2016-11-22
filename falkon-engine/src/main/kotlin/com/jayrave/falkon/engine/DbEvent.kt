package com.jayrave.falkon.engine

/**
 * Carries the type of event that took place (insert, update, delete & insert or replace) &
 * the table it took place in
 */
data class DbEvent private constructor(val type: Type, val tableName: String) {

    enum class Type {
        INSERT,
        UPDATE,
        DELETE,
        INSERT_OR_REPLACE
    }


    companion object {
        fun create(type: Type, tableName: String) = DbEvent(type, tableName)
        fun forInsert(tableName: String) = create(Type.INSERT, tableName)
        fun forUpdate(tableName: String) = create(Type.UPDATE, tableName)
        fun forDelete(tableName: String) = create(Type.DELETE, tableName)
        fun forInsertOrReplace(tableName: String) = create(Type.INSERT_OR_REPLACE, tableName)
    }
}


/**
 * Interface definition for callbacks to be invoked when a [DbEvent] takes place
 */
interface DbEventListener {
    fun onEvent(dbEvent: DbEvent)
    fun onEvents(dbEvents: Iterable<DbEvent>)
}