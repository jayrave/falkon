package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteOpenHelper
import com.jayrave.falkon.engine.*
import com.jayrave.falkon.engine.CompiledQuery
import java.sql.SQLException

class AndroidSqliteEngineCore(sqLiteOpenHelper: SQLiteOpenHelper) : EngineCore {

    private val database by lazy { sqLiteOpenHelper.writableDatabase }

    override fun <R> executeInTransaction(operation: () -> R): R {
        if (isInTransaction()) {
            throw SQLException("Transactions can't be nested")
        }

        val result: R
        database.beginTransaction()
        try {
            result = operation.invoke()
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }

        return result
    }


    override fun isInTransaction(): Boolean {
        return database.inTransaction()
    }


    override fun compileSql(rawSql: String): CompiledStatement<Unit> {
        return UnitReturningCompiledStatement(rawSql, database)
    }


    override fun compileInsert(rawSql: String): CompiledInsert {
        return IUD_CompiledStatement(rawSql, database)
    }


    override fun compileUpdate(rawSql: String): CompiledUpdate {
        return IUD_CompiledStatement(rawSql, database)
    }


    override fun compileDelete(rawSql: String): CompiledDelete {
        return IUD_CompiledStatement(rawSql, database)
    }


    /**
     * This method doesn't return a true compiled statement. Every time the returned statement
     * is executed, the passed in SQL string goes through a compilation phase
     *
     * @see CompiledQuery
     */
    override fun compileQuery(rawSql: String): com.jayrave.falkon.engine.CompiledQuery {
        return CompiledQuery(rawSql, database)
    }
}