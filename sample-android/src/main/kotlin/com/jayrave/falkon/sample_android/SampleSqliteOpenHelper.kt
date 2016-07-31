package com.jayrave.falkon.sample_android

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * TODO - how to break the vicious circular dependency between this, AndroidSqliteEngine & Tables?
 */
class SampleSqliteOpenHelper(context: Context) :
        SQLiteOpenHelper(context, "falkon_sample_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Do create related stuff here
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Do upgrade related stuff here
    }
}