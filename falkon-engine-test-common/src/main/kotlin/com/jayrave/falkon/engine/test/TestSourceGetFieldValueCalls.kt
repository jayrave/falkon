package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.safeCloseAfterExecution
import org.assertj.core.api.Assertions.assertThat

class TestSourceGetFieldValueCalls(engine: Engine) :
        BaseClassForTestingSourceImplementation(engine) {

    fun `#getColumnIndex throws for column name not in the result set`() {
        insertRecord(seed = 5)
        queryForAll { source ->
            assertThat(source.moveToNext()).isTrue()
            failIfOpDoesNotThrow { source.getColumnIndex("this is a crazy column name") }
        }
    }


    fun `test #getColumnIndex & #get* Calls`() {
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
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7L)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .safeCloseAfterExecution()

        queryForAll { s ->
            assertThat(s.moveToNext()).isTrue()
            assertThat(s.getShort(s.getColumnIndex(SHORT_COLUMN_NAME))).isEqualTo(5)
            assertThat(s.getInt(s.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(6)
            assertThat(s.getLong(s.getColumnIndex(LONG_COLUMN_NAME))).isEqualTo(7L)
            assertThat(s.getFloat(s.getColumnIndex(FLOAT_COLUMN_NAME))).isEqualTo(8F)
            assertThat(s.getDouble(s.getColumnIndex(DOUBLE_COLUMN_NAME))).isEqualTo(9.0)
            assertThat(s.getString(s.getColumnIndex(STRING_COLUMN_NAME))).isEqualTo("test 10")
            assertThat(s.getBlob(s.getColumnIndex(BLOB_COLUMN_NAME))).isEqualTo(byteArrayOf(11))
        }
    }


    fun `test #getColumnIndex & #isNull Calls`() {
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
                .bindNull(1, Type.SHORT)
                .bindNull(2, Type.INT)
                .bindNull(3, Type.LONG)
                .bindNull(4, Type.FLOAT)
                .bindNull(5, Type.DOUBLE)
                .bindNull(6, Type.STRING)
                .bindNull(7, Type.BLOB)
                .safeCloseAfterExecution()

        queryForAll { s ->
            assertThat(s.moveToNext()).isTrue()
            assertThat(s.isNull(s.getColumnIndex(SHORT_COLUMN_NAME))).isTrue()
            assertThat(s.isNull(s.getColumnIndex(INT_COLUMN_NAME))).isTrue()
            assertThat(s.isNull(s.getColumnIndex(LONG_COLUMN_NAME))).isTrue()
            assertThat(s.isNull(s.getColumnIndex(FLOAT_COLUMN_NAME))).isTrue()
            assertThat(s.isNull(s.getColumnIndex(DOUBLE_COLUMN_NAME))).isTrue()
            assertThat(s.isNull(s.getColumnIndex(STRING_COLUMN_NAME))).isTrue()
            assertThat(s.isNull(s.getColumnIndex(BLOB_COLUMN_NAME))).isTrue()
        }
    }


    private fun queryForAll(test: (Source) -> Any?) {
        val cs = engine.compileQuery(listOf(TABLE_NAME), SELECT_ALL_SQL)
        val source = cs.execute()
        try {
            test.invoke(source)
        } finally {
            source.close()
            cs.close()
        }
    }
}