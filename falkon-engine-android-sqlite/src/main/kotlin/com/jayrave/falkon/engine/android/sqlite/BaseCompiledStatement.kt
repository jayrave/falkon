package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Type

internal abstract class BaseCompiledStatement<T>(
        override val sql: String, database: SQLiteDatabase) :
        CompiledStatement<T> {

    protected val sqliteStatement: SQLiteStatement = database.compileStatement(sql)

    override fun bindShort(index: Int, value: Short): CompiledStatement<T> {
        sqliteStatement.bindLong(index, value.toLong())
        return this
    }

    override fun bindInt(index: Int, value: Int): CompiledStatement<T> {
        sqliteStatement.bindLong(index, value.toLong())
        return this
    }

    override fun bindLong(index: Int, value: Long): CompiledStatement<T> {
        sqliteStatement.bindLong(index, value)
        return this
    }

    override fun bindFloat(index: Int, value: Float): CompiledStatement<T> {
        sqliteStatement.bindDouble(index, value.toDouble())
        return this
    }

    override fun bindDouble(index: Int, value: Double): CompiledStatement<T> {
        sqliteStatement.bindDouble(index, value)
        return this
    }

    override fun bindString(index: Int, value: String): CompiledStatement<T> {
        sqliteStatement.bindString(index, value)
        return this
    }

    override fun bindBlob(index: Int, value: ByteArray): CompiledStatement<T> {
        sqliteStatement.bindBlob(index, value)
        return this
    }

    override fun bindNull(index: Int, type: Type): CompiledStatement<T> {
        sqliteStatement.bindNull(index)
        return this
    }

    override fun close() {
        sqliteStatement.close()
    }

    override fun clearBindings(): CompiledStatement<T> {
        sqliteStatement.clearBindings()
        return this
    }
}