package com.jayrave.falkon.engine

data class DbEvent private constructor(val type: Type, val tableNames: Iterable<String>) {

    enum class Type {
        INSERT,
        UPDATE,
        DELETE
    }


    companion object {
        fun forInsert(tableName: String) = DbEvent(Type.INSERT, listOf(tableName))
        fun forUpdate(tableName: String) = DbEvent(Type.UPDATE, listOf(tableName))
        fun forDelete(tableName: String) = DbEvent(Type.DELETE, listOf(tableName))
    }
}


interface DbEventListener {
    fun onEvent(dbEvent: DbEvent)
    fun onEvents(dbEvents: Iterable<DbEvent>)
}