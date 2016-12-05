package com.jayrave.falkon.engine.jdbc

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Connection
import java.sql.PreparedStatement

/**
 * Other APIs of [BaseCompiledStatement] are tested in
 * [CompiledStatementsWithBoundArgsIntegrationTests]
 */
class BaseCompiledStatementTest {

    @Test
    fun `closure`() {
        // Setup mocks
        val mockStatement = mock<PreparedStatement>()
        val mockConnectionManager = mock<ConnectionManager>()
        val mockConnection = mock<Connection>()
        whenever(mockConnectionManager.acquireConnection()).thenReturn(mockConnection)

        val cs = CompiledStatementForTest(mockConnectionManager, mockStatement)
        assertThat(cs.isClosed).isFalse()
        verifyZeroInteractions(mockStatement)
        verify(mockConnectionManager).acquireConnection()
        assertThat(cs.close())
        assertThat(cs.isClosed).isTrue()

        // Assert that statement was closed & connection was released
        verify(mockStatement).close()
        verify(mockConnectionManager).releaseConnection(eq(mockConnection))
    }


    @Test
    fun `clear bindings`() {
        // Setup mocks
        val mockStatement = mock<PreparedStatement>()
        val mockConnectionManager = mock<ConnectionManager>()
        whenever(mockConnectionManager.acquireConnection()).thenReturn(mock<Connection>())

        val cs = CompiledStatementForTest(mockConnectionManager, mockStatement)
        verifyZeroInteractions(mockStatement)
        cs.clearBindings()
        verify(mockStatement).clearParameters()
    }



    private class CompiledStatementForTest(
            cm: ConnectionManager, private val ps: PreparedStatement) :
            BaseCompiledStatement<Int>("DUMMY SQL", cm) {

        private fun exception() = UnsupportedOperationException("not implemented")
        override fun execute() = throw exception()
        override fun prepareStatement(connection: Connection) = ps
    }
}