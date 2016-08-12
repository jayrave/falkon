package com.jayrave.falkon.engine

data class DbEvent private constructor(val type: Type, val tableName: String) {

    enum class Type {
        INSERT,
        UPDATE,
        DELETE
    }


    companion object {
        fun create(type: Type, tableName: String) = DbEvent(type, tableName)
        fun forInsert(tableName: String) = create(Type.INSERT, tableName)
        fun forUpdate(tableName: String) = create(Type.UPDATE, tableName)
        fun forDelete(tableName: String) = create(Type.DELETE, tableName)
    }
}


interface DbEventListener {
    fun onEvent(dbEvent: DbEvent)
    fun onEvents(dbEvents: Iterable<DbEvent>)
}