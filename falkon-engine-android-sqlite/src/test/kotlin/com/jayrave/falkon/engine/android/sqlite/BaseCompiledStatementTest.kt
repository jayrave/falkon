package com.jayrave.falkon.engine.android.sqlite

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteStatement
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
        val mockStatement = mock<SupportSQLiteStatement>()
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
        val mockStatement = mock<SupportSQLiteStatement>()
        val db = buildMockDb(mockStatement)
        val cs = CompiledStatementForTest(db)

        verifyZeroInteractions(mockStatement)
        cs.clearBindings()
        verify(mockStatement).clearBindings()
    }



    private class CompiledStatementForTest(db: SupportSQLiteDatabase) :
            BaseCompiledStatement<Int>("DUMMY SQL", db) {

        override fun execute(): Int = throw UnsupportedOperationException("not implemented")
    }



    companion object {
        private fun buildMockDb(sqLiteStatement: SupportSQLiteStatement): SupportSQLiteDatabase {
            val db = mock<SupportSQLiteDatabase>()
            whenever(db.compileStatement(any())).thenReturn(sqLiteStatement)
            return db
        }
    }
}