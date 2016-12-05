package com.jayrave.falkon.engine.android.sqlite

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Other APIs of [BaseCompiledStatement] are tested in
 * [CompiledStatementsWithBoundArgsIntegrationTests]
 */
class BaseCompiledStatementTest {

    @Test
    fun `closure`() {
        val mockStatement = mock<SQLiteStatement>()
        val db = buildMockDb(mockStatement)
        val cs = CompiledStatementForTest(db)

        assertThat(cs.isClosed).isFalse()
        verifyZeroInteractions(mockStatement)
        assertThat(cs.close())
        verify(mockStatement).close()
        assertThat(cs.isClosed).isTrue()
    }


    @Test
    fun `clear bindings`() {
        val mockStatement = mock<SQLiteStatement>()
        val db = buildMockDb(mockStatement)
        val cs = CompiledStatementForTest(db)

        verifyZeroInteractions(mockStatement)
        cs.clearBindings()
        verify(mockStatement).clearBindings()
    }



    private class CompiledStatementForTest(db: SQLiteDatabase) :
            BaseCompiledStatement<Int>("DUMMY SQL", db) {

        override fun execute(): Int = throw UnsupportedOperationException("not implemented")
    }



    companion object {
        private fun buildMockDb(sqLiteStatement: SQLiteStatement): SQLiteDatabase {
            val db = mock<SQLiteDatabase>()
            whenever(db.compileStatement(any())).thenReturn(sqLiteStatement)
            return db
        }
    }
}