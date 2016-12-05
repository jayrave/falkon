package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.safeCloseAfterExecution

abstract class BaseClassForTestingSourceImplementation(protected val engine: Engine) {

    init {
        val createTableSql = "CREATE TABLE $TABLE_NAME (" +
                "$SHORT_COLUMN_NAME SMALLINT, " +
                "$INT_COLUMN_NAME INTEGER, " +
                "$LONG_COLUMN_NAME BIGINT, " +
                "$FLOAT_COLUMN_NAME REAL, " +
                "$DOUBLE_COLUMN_NAME DOUBLE, " +
                "$STRING_COLUMN_NAME VARCHAR, " +
                "$BLOB_COLUMN_NAME BLOB" +
                ")"

        engine
                .compileSql(null, createTableSql)
                .safeCloseAfterExecution()
    }


    protected fun insertRecord(seed: Int) {
        val insertSql = "INSERT INTO $TABLE_NAME (" +
                "$SHORT_COLUMN_NAME, " +
                "$INT_COLUMN_NAME, " +
                "$LONG_COLUMN_NAME, " +
                "$FLOAT_COLUMN_NAME, " +
                "$DOUBLE_COLUMN_NAME, " +
                "$STRING_COLUMN_NAME, " +
                "$BLOB_COLUMN_NAME) VALUES (?, ?, ?, ?, ?, ?, ?)"

        engine
                .compileInsert(TABLE_NAME, insertSql)
                .bindShort(1, seed.toShort())
                .bindInt(2, seed)
                .bindLong(3, seed.toLong())
                .bindFloat(4, seed.toFloat())
                .bindDouble(5, seed.toDouble())
                .bindString(6, "$seed")
                .bindBlob(7, byteArrayOf(seed.toByte()))
                .safeCloseAfterExecution()
    }



    companion object {
        const val TABLE_NAME = "test"
        const val SHORT_COLUMN_NAME = "column_short"
        const val INT_COLUMN_NAME = "column_int"
        const val LONG_COLUMN_NAME = "column_long"
        const val FLOAT_COLUMN_NAME = "column_float"
        const val DOUBLE_COLUMN_NAME = "column_double"
        const val STRING_COLUMN_NAME = "column_string"
        const val BLOB_COLUMN_NAME = "column_blob"

        const val SELECT_ALL_SQL = "SELECT * FROM $TABLE_NAME"
    }
}