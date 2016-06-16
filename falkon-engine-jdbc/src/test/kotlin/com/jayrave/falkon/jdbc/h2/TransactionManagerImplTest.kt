package com.jayrave.falkon.jdbc.h2

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

class TransactionManagerImplTest {

    private val manager: TransactionManager
    init {
        val dataSourceMock = mock<DataSource>()
        whenever(dataSourceMock.connection).thenReturn(mock())
        manager = TransactionManagerImpl(dataSourceMock)
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
                assertThat(connection2).isNotSameAs(connection1)

                manager.executeInTransaction {
                    val connection3 = manager.getConnectionIfInTransaction()
                    assertThat(connection3).isNotNull()
                    assertThat(manager.belongsToTransaction(connection3!!)).isTrue()
                    assertThat(connection3).isNotSameAs(connection1)
                    assertThat(connection3).isNotSameAs(connection2)

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


    private fun assertConnectionsAreNotInTransaction(connections: List<Connection>) {
        connections.forEach { assertThat(manager.belongsToTransaction(it)).isFalse() }
    }
}