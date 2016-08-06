package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.assertj.core.api.Assertions.fail
import org.robolectric.RuntimeEnvironment

fun buildAndroidSqliteEngineCore(
        helper: SQLiteOpenHelper = SqliteOpenHelperForTest()):
        AndroidSqliteEngineCore {

    return AndroidSqliteEngineCore(helper)
}


class SqliteOpenHelperForTest : SQLiteOpenHelper(RuntimeEnvironment.application, null, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        // No op
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        fail("onUpgrade shouldn't be called")
    }
}