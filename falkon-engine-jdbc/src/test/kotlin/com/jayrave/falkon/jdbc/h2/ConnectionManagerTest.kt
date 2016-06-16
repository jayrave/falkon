package com.jayrave.falkon.jdbc.h2

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Connection
import javax.sql.DataSource

class ConnectionManagerTest {

    private val dataSourceMock = mock<DataSource>()
    private val transactionManagerMock = mock<TransactionManager>()
    private val connectionManager = ConnectionManager(dataSourceMock, transactionManagerMock)

    @Test
    fun testAcquireConnectionReturnsNonNullConnectionFromTransactionManager() {
        val connectionMock = mock<Connection>()
        whenever(transactionManagerMock.getConnectionIfInTransaction()).thenReturn(connectionMock)

        assertThat(connectionManager.acquireConnection()).isSameAs(connectionMock)
        verify(transactionManagerMock).getConnectionIfInTransaction()
        verifyNoMoreInteractions(transactionManagerMock)
        verifyZeroInteractions(dataSourceMock)
    }

    @Test
    fun testAcquireConnectionReturnsConnectionFromDataSourceIfThereIsNotATransaction() {
        val connectionMock = mock<Connection>()
        whenever(transactionManagerMock.getConnectionIfInTransaction()).thenReturn(null)
        whenever(dataSourceMock.connection).thenReturn(connectionMock)

        assertThat(connectionManager.acquireConnection()).isSameAs(connectionMock)
        verify(transactionManagerMock).getConnectionIfInTransaction()
        verifyNoMoreInteractions(transactionManagerMock)
        verify(dataSourceMock).connection
        verifyNoMoreInteractions(dataSourceMock)
    }

    @Test
    fun testReleaseConnectionDoesNothingIfConnectionBelongsToTransaction() {
        val connectionMock = mock<Connection>()
        whenever(transactionManagerMock.belongsToTransaction(any())).thenReturn(true)
        connectionManager.releaseConnection(connectionMock)

        verify(transactionManagerMock).belongsToTransaction(eq(connectionMock))
        verifyNoMoreInteractions(transactionManagerMock)
        verifyZeroInteractions(connectionMock)
    }

    @Test
    fun testReleaseConnectionClosesConnectionIfItDoesNotBelongToATransaction() {
        val connectionMock = mock<Connection>()
        whenever(transactionManagerMock.belongsToTransaction(any())).thenReturn(false)
        connectionManager.releaseConnection(connectionMock)

        verify(transactionManagerMock).belongsToTransaction(eq(connectionMock))
        verifyNoMoreInteractions(transactionManagerMock)
        verify(connectionMock).close()
        verifyNoMoreInteractions(connectionMock)
    }
}