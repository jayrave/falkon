package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Type

internal abstract class BaseCompiledStatement<T>(
        final override val sql: String, database: SQLiteDatabase) :
        CompiledStatement<T> {

    protected val sqliteStatement: SQLiteStatement = database.compileStatement(sql)
    override final var isClosed = false
        private set

    override final fun bindShort(index: Int, value: Short): CompiledStatement<T> {
        sqliteStatement.bindLong(index, value.toLong())
        return this
    }

    override final fun bindInt(index: Int, value: Int): CompiledStatement<T> {
        sqliteStatement.bindLong(index, value.toLong())
        return this
    }

    override final fun bindLong(index: Int, value: Long): CompiledStatement<T> {
        sqliteStatement.bindLong(index, value)
        return this
    }

    override final fun bindFloat(index: Int, value: Float): CompiledStatement<T> {
        sqliteStatement.bindDouble(index, value.toDouble())
        return this
    }

    override final fun bindDouble(index: Int, value: Double): CompiledStatement<T> {
        sqliteStatement.bindDouble(index, value)
        return this
    }

    override final fun bindString(index: Int, value: String): CompiledStatement<T> {
        sqliteStatement.bindString(index, value)
        return this
    }

    override final fun bindBlob(index: Int, value: ByteArray): CompiledStatement<T> {
        sqliteStatement.bindBlob(index, value)
        return this
    }

    override final fun bindNull(index: Int, type: Type): CompiledStatement<T> {
        sqliteStatement.bindNull(index)
        return this
    }

    override final fun close() {
        sqliteStatement.close()
        isClosed = true
    }

    override final fun clearBindings(): CompiledStatement<T> {
        sqliteStatement.clearBindings()
        return this
    }
}