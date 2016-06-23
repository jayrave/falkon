package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jayrave.falkon.engine.test.NativeQueryExecutor
import com.jayrave.falkon.engine.test.NativeSqlExecutor
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseClassForIntegrationTests : RobolectricTestBaseClass() {

    protected lateinit var database: SQLiteDatabase
    protected lateinit var engine: AndroidSqliteEngine

    protected val sqlExecutorUsingDataSource = object : NativeSqlExecutor {
        override fun execute(sql: String) = database.execSQL(sql)
    }

    protected val queryExecutorUsingDataSource = object : NativeQueryExecutor {
        override fun execute(query: String) = CursorBackedSource(database.rawQuery(query, null))
    }


    @Before
    fun setUp() {
        val helper = OpenHelper()
        database = helper.writableDatabase
        engine = AndroidSqliteEngine(helper)
    }


    @After
    fun tearDown() {
        RuntimeEnvironment.application.deleteDatabase(database.path)
    }


    private class OpenHelper : SQLiteOpenHelper(RuntimeEnvironment.application, null, null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            // No op
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            fail("onUpgrade shouldn't be called")
        }
    }
}