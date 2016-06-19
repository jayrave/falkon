package com.jayrave.falkon.engine.jdbc

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.sql.Connection
import java.util.*
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
            val connection1 = manager.getConnectionIfInTransaction()!!
            assertThat(connection1).isInstanceOf(ConnectionWrapperForTransaction::class.java)

            manager.executeInTransaction {
                val connection2 = manager.getConnectionIfInTransaction()!!
                assertThat(connection2).isInstanceOf(ConnectionWrapperForTransaction::class.java)
            }
        }
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
    fun testNestedTransactions() {
        val doneConnections = ArrayList<Connection>()

        manager.executeInTransaction {
            val connection1 = manager.getConnectionIfInTransaction()
            assertThat(connection1).isNotNull()
            assertThat(manager.belongsToTransaction(connection1!!)).isTrue()

            manager.executeInTransaction {
                val connection2 = manager.getConnectionIfInTransaction()
                assertThat(connection2).isNotNull()
                assertThat(manager.belongsToTransaction(connection2!!)).isTrue()
                assertConnectionsFromTransactionManagerAreNotTheSame(connection2, connection1)

                manager.executeInTransaction {
                    val connection3 = manager.getConnectionIfInTransaction()
                    assertThat(connection3).isNotNull()
                    assertThat(manager.belongsToTransaction(connection3!!)).isTrue()
                    assertConnectionsFromTransactionManagerAreNotTheSame(connection3, connection1)
                    assertConnectionsFromTransactionManagerAreNotTheSame(connection3, connection2)

                    doneConnections.add(connection3)
                }

                val connection2Again = manager.getConnectionIfInTransaction()
                assertThat(connection2Again).isSameAs(connection2)
                assertThat(manager.belongsToTransaction(connection2Again!!)).isTrue()
                assertConnectionsAreNotInTransaction(doneConnections)

                doneConnections.add(connection2)
            }

            val connection1Again = manager.getConnectionIfInTransaction()
            assertThat(connection1Again).isSameAs(connection1)
            assertThat(manager.belongsToTransaction(connection1Again!!)).isTrue()
            assertConnectionsAreNotInTransaction(doneConnections)

            doneConnections.add(connection1)
        }

        assertConnectionsAreNotInTransaction(doneConnections)
    }


    /**
     * Asserts that connections are not the same and also the delegates that each connection
     * uses is a different one too
     */
    private fun assertConnectionsFromTransactionManagerAreNotTheSame(
            connection1: Connection, connection2: Connection) {

        assertThat(connection1).isNotSameAs(connection2)
        assertThat((connection1 as ConnectionWrapperForTransaction).delegate).isNotSameAs(
                (connection2 as ConnectionWrapperForTransaction).delegate
        )
    }


    private fun assertConnectionsAreNotInTransaction(connections: List<Connection>) {
        connections.forEach { assertThat(manager.belongsToTransaction(it)).isFalse() }
    }
}