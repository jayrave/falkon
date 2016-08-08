package com.jayrave.falkon.engine

data class DbEvent private constructor(val type: Type, val tableName: String) {

    enum class Type {
        INSERT,
        UPDATE,
        DELETE
    }


    companion object {
        fun forInsert(tableName: String) = DbEvent(Type.INSERT, tableName)
        fun forUpdate(tableName: String) = DbEvent(Type.UPDATE, tableName)
        fun forDelete(tableName: String) = DbEvent(Type.DELETE, tableName)
    }
}


interface DbEventListener {
    fun onEvent(dbEvent: DbEvent)
    fun onEvents(dbEvents: Iterable<DbEvent>)
}