package com.jayrave.falkon.engine.android.sqlite

import androidx.sqlite.db.SupportSQLiteDatabase
import com.jayrave.falkon.engine.test.NativeQueryExecutor
import com.jayrave.falkon.engine.test.NativeSqlExecutor
import org.junit.After
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class BaseClassForIntegrationTests : RobolectricTestBaseClass() {

    protected lateinit var database: SupportSQLiteDatabase
    protected lateinit var engineCore: AndroidSqliteEngineCore

    protected val sqlExecutorUsingDataSource = object : NativeSqlExecutor {
        override fun execute(sql: String) = database.execSQL(sql)
    }

    protected val queryExecutorUsingDataSource = object : NativeQueryExecutor {
        override fun execute(query: String) = CursorBackedSource(database.query(query))
    }


    @Before
    fun setUp() {
        val helper = buildSqliteOpenHelper(RuntimeEnvironment.application)
        database = helper.writableDatabase
        engineCore = AndroidSqliteEngineCore(helper)
    }


    @After
    fun tearDown() {
        RuntimeEnvironment.application.deleteDatabase(database.path)
    }
}