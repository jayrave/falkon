package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import com.jayrave.falkon.engine.test.NativeQueryExecutor
import com.jayrave.falkon.engine.test.NativeSqlExecutor
import org.junit.After
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseClassForIntegrationTests : RobolectricTestBaseClass() {

    protected lateinit var database: SQLiteDatabase
    protected lateinit var engineCore: AndroidSqliteEngineCore

    protected val sqlExecutorUsingDataSource = object : NativeSqlExecutor {
        override fun execute(sql: String) = database.execSQL(sql)
    }

    protected val queryExecutorUsingDataSource = object : NativeQueryExecutor {
        override fun execute(query: String) = CursorBackedSource(database.rawQuery(query, null))
    }


    @Before
    fun setUp() {
        val helper = SqliteOpenHelperForTest()
        database = helper.writableDatabase
        engineCore = AndroidSqliteEngineCore(helper)
    }


    @After
    fun tearDown() {
        RuntimeEnvironment.application.deleteDatabase(database.path)
    }
}