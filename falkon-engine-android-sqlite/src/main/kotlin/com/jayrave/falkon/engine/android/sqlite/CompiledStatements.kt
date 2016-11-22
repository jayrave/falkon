package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase

/**
 * Could be used for compiling & executing any valid SQL statement
 */
internal class UnitReturningCompiledStatement(sql: String, database: SQLiteDatabase) :
        BaseCompiledStatement<Unit>(sql, database) {

    override fun execute() {
        sqliteStatement.execute()
    }
}



/**
 * For compiling & executing
 *  - INSERT
 *  - UPDATE
 *  - DELETE
 *  - INSERT OR REPLACE
 */
internal class IUD_CompiledStatement(sql: String, database: SQLiteDatabase) :
        BaseCompiledStatement<Int>(sql, database) {

    override fun execute(): Int {
        return sqliteStatement.executeUpdateDelete()
    }
}