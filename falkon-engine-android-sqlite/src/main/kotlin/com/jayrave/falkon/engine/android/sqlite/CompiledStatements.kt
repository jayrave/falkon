package com.jayrave.falkon.engine.android.sqlite

import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Could be used for compiling & executing any valid SQL statement
 */
internal class UnitReturningCompiledStatement(sql: String, database: SupportSQLiteDatabase) :
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
internal class IUD_CompiledStatement(sql: String, database: SupportSQLiteDatabase) :
        BaseCompiledStatement<Int>(sql, database) {

    override fun execute(): Int {
        return sqliteStatement.executeUpdateDelete()
    }
}