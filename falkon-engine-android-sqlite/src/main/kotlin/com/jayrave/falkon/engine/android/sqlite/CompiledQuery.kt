package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.Type
import java.sql.SQLException
import java.util.*

/**
 * This isn't actually a compiled statement as there is no way (that Jay could find) in Android
 * to compile a SELECT statement!! This class just stores the sql & the bind args, and when
 * [execute] is called, send them off to the database
 *
 * *CAUTION: * This class is not thread-safe
 */
internal class CompiledQuery(override val sql: String, private val database: SQLiteDatabase) :
        CompiledStatement<Source> {

    private var isClosed = false
    private var bindArgs: MutableMap<Int, Any?> = newArgsMap()

    override fun execute(): Source {
        throwIfClosed()
        return CursorBackedSource(database.rawQueryWithFactory(
                ArgsBindingCursorFactory(bindArgs), sql, null, null
        ))
    }

    override fun bindShort(index: Int, value: Short): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindInt(index: Int, value: Int): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindLong(index: Int, value: Long): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindFloat(index: Int, value: Float): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindDouble(index: Int, value: Double): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindString(index: Int, value: String): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindBlob(index: Int, value: ByteArray): CompiledStatement<Source> {
        bindArg(index, value)
        return this
    }

    override fun bindNull(index: Int, type: Type): CompiledStatement<Source> {
        bindArg(index, null)
        return this
    }

    override fun close() {
        isClosed = true // Idempotent. Can call multiple times
    }

    override fun clearBindings(): CompiledStatement<Source> {
        throwIfClosed()
        bindArgs = newArgsMap()
        return this
    }

    private fun bindArg(index: Int, value: Any?) {
        throwIfClosed()
        bindArgs.put(index, value)
    }

    private fun throwIfClosed() {
        if (isClosed) {
            throw SQLException("CompiledQuery has already been closed!!")
        }
    }


    companion object {
        private fun newArgsMap(): MutableMap<Int, Any?> = HashMap()
    }
}