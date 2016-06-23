package com.jayrave.falkon.engine.android.sqlite

import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQuery

/**
 * A class to workaround the issue of binding all kinds of selection args and not just an array
 * of strings (as the Android API demands)
 */
internal class ArgsBindingCursorFactory(val argsMap: Map<Int, Any?>) :
        SQLiteDatabase.CursorFactory {

    override fun newCursor(
            db: SQLiteDatabase, masterQuery: SQLiteCursorDriver,
            editTable: String?, query: SQLiteQuery): Cursor? {

        // Bind args & create and return cursor
        argsMap.entries.forEach { DatabaseUtils.bindObjectToProgram(query, it.key, it.value) }
        return SQLiteCursor(masterQuery, editTable, query)
    }
}