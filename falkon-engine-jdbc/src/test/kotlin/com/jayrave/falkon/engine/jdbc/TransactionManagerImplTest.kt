package com.jayrave.falkon.engine.jdbc

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class TransactionManagerImplTest {

    private val manager: TransactionManager
    init {
        val dataSourceMock = mock<DataSource>()
        whenever(dataSourceMock.connection).thenAnswer { mock<Connection>() }
        manager = TransactionManagerImpl(dataSourceMock)
    }


    @Test
    fun testConnectionWrapperForTransactionIsUsedToPreventOutsideMeddlingOfConnections() {
        manager.executeInTransaction {
            assertThat(manager.getConnectionIfInTransaction()!!).isInstanceOf(
                    ConnectionWrapperForTransaction::class.java
            )
        }
    }


    @Test
    fun testIsInTransaction() {
        assertThat(manager.isInTransaction()).isFalse()
        manager.executeInTransaction { assertThat(manager.isInTransaction()).isTrue() }
        assertThat(manager.isInTransaction()).isFalse()
    }


    @Test
    fun testGetConnectionReturnsAValidConnectionWhenInTransaction() {
        manager.executeInTransaction {
            val connection = manager.getConnectionIfInTransaction()
            assertThat(connection).isNotNull()
        }
    }


    @Test
    fun testGetConnectionIfInTransactionReturnsNullWhenNotInTransaction() {
        assertThat(manager.getConnectionIfInTransaction()).isNull()
    }


    @Test
    fun testBelongsToTransactionReturnsTrueForConnectionInTransaction() {
        manager.executeInTransaction {
            val connection = manager.getConnectionIfInTransaction()
            assertThat(connection).isNotNull()
            assertThat(manager.belongsToTransaction(connection!!)).isTrue()
        }
    }


    @Test
    fun testBelongsToTransactionReturnsFalseForConnectionNotInTransaction() {
        manager.executeInTransaction {
            assertThat(manager.belongsToTransaction(mock())).isFalse()
        }
    }


    @Test
    fun testConnectionIsCommittedAndClosedOnSuccessfulTransaction() {
        var connection: Connection? = null
        manager.executeInTransaction {
            connection = manager.getConnectionIfInTransaction()!!
        }

        val capturedConnection = (connection as ConnectionWrapperForTransaction).delegate
        verify(capturedConnection).autoCommit = false
        verify(capturedConnection).commit()
        verify(capturedConnection).autoCommit = true
        verify(capturedConnection).close()
        verifyNoMoreInteractions(capturedConnection)
    }


    @Test
    fun testConnectionIsRolledBackAndClosedOnException() {
        var connection: Connection? = null
        var expectedExceptionWasThrown = false
        val expectedException = RuntimeException()

        try {
            manager.executeInTransaction {
                connection = manager.getConnectionIfInTransaction()!!
                throw expectedException
            }

        } catch (e: Exception) {
            assertThat(e).isSameAs(expectedException)
            expectedExceptionWasThrown = true
        }

        if (!expectedExceptionWasThrown) {
            fail("Exception inside transaction didn't get propagated!!")

        } else {
            val capturedConnection = (connection as ConnectionWrapperForTransaction).delegate
            verify(capturedConnection).autoCommit = false
            verify(capturedConnection).rollback()
            verify(capturedConnection).autoCommit = true
            verify(capturedConnection).close()
            verifyNoMoreInteractions(capturedConnection)
        }
    }


    @Test
    fun testStateAfterTransaction() {
        // Execute a dummy transaction
        manager.executeInTransaction { manager.getConnectionIfInTransaction()!! }

        assertThat(manager.getConnectionIfInTransaction()).isNull()
        assertThat(manager.belongsToTransaction(mock())).isFalse()
    }


    @Test
    fun testNestingTransactionsThrows() {
        var exceptionCaught = false
        manager.executeInTransaction {
            try {
                manager.executeInTransaction {}
            } catch (e: SQLException) {
                exceptionCaught = true
            }
        }

        assertThat(exceptionCaught).isTrue()
    }
}