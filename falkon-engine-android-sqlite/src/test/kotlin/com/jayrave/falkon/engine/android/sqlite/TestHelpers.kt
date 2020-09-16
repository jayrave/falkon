package com.jayrave.falkon.engine.android.sqlite

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import org.assertj.core.api.Assertions.fail

fun buildAndroidSqliteEngineCore(context: Context): AndroidSqliteEngineCore {
    return AndroidSqliteEngineCore(buildSqliteOpenHelper(context))
}

fun buildSqliteOpenHelper(context: Context): SupportSQLiteOpenHelper {
    return FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                    .callback(Callback())
                    .build()
    )
}


private class Callback : SupportSQLiteOpenHelper.Callback(1) {
    override fun onCreate(db: SupportSQLiteDatabase) {
        // No op
    }

    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
        fail("onUpgrade shouldn't be called")
    }
}