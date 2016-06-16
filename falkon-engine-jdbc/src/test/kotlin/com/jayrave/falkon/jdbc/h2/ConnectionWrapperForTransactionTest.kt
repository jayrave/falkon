package com.jayrave.falkon.jdbc.h2

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import java.sql.Connection

class ConnectionWrapperForTransactionTest {

    private val connectionMock = mock<Connection>()
    private val connectionWrapper = ConnectionWrapperForTransaction(connectionMock)

    @Test(expected = UnsupportedOperationException::class)
    fun testAbort() {
        connectionWrapper.abort(null)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testClose() {
        connectionWrapper.close()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testCommit() {
        connectionWrapper.commit()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testReleaseSavepoint() {
        connectionWrapper.releaseSavepoint(null)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testRollback() {
        connectionWrapper.rollback()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testRollback1() {
        connectionWrapper.rollback(null)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testSetAutoCommit() {
        connectionWrapper.autoCommit = false
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testSetSavepoint() {
        connectionWrapper.setSavepoint()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testSetSavepoint1() {
        connectionWrapper.setSavepoint("test")
    }

    @Test(expected = UnsupportedOperationException::class)
    fun testSetTransactionIsolation() {
        connectionWrapper.transactionIsolation = 0
    }
}