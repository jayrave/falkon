package com.jayrave.falkon.engine

import com.jayrave.falkon.engine.testLib.StoringLogger
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LoggingCompiledStatementTest {

    @Test
    fun testSuccessfulExecution() {
        val sql = "this is a SQL for test"
        val arg = "test arg"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildSuccessfullyExecutingCompiledStatement(sql), storingLogger
        )

        // bind & execute
        loggingCompiledStatement.bindString(1, arg).execute()

        // assert info passed to logger
        assertThat(storingLogger.onExecutionFailed).isNull()
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).containsOnly(arg)
    }


    @Test
    fun testExecutionFailed() {
        val sql = "this is a another SQL for test"
        val arg = "test one more arg"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildCompiledStatementThatThrowsOnExecuting(sql), storingLogger
        )

        // bind & execute
        var exceptionWasThrown = false
        try {
            loggingCompiledStatement.bindString(1, arg).execute()
        } catch (e: Exception) {
           exceptionWasThrown = true
        }

        // assert info passed to logger
        assertThat(exceptionWasThrown).isTrue()
        assertThat(storingLogger.onSuccessfulExecution).isNull()
        assertThat(storingLogger.onExecutionFailed?.sql).isEqualTo(sql)
        assertThat(storingLogger.onExecutionFailed?.arguments).containsOnly(arg)
    }


    @Test
    fun testExecuteWithoutBoundArgs() {
        val sql = "this is a SQL for test"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildSuccessfullyExecutingCompiledStatement(sql), storingLogger
        )

        // execute
        loggingCompiledStatement.execute()

        // assert info passed to logger
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).isEmpty()
    }


    @Test
    fun testExecuteWithAllArgs() {
        val sql = "this is a SQL for test"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildSuccessfullyExecutingCompiledStatement(sql), storingLogger
        )

        // execute
        loggingCompiledStatement
                .bindShort(1, 5)
                .bindInt(2, 6)
                .bindLong(3, 7)
                .bindFloat(4, 8F)
                .bindDouble(5, 9.0)
                .bindString(6, "test 10")
                .bindBlob(7, byteArrayOf(11))
                .bindNull(8, Type.STRING)
                .execute()

        // assert info passed to logger
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).containsExactly(
                5.toShort(), 6, 7L, 8F, 9.0, "test 10", byteArrayOf(11), null
        )
    }


    @Test
    fun testRebindingArgs() {
        val sql = "this is a SQL for test"
        val initialArg = "test initial"
        val reboundArg = "rebound test"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildSuccessfullyExecutingCompiledStatement(sql), storingLogger
        )

        // execute
        loggingCompiledStatement
                .bindString(1, initialArg)
                .bindString(1, reboundArg)
                .execute()

        // assert info passed to logger
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).containsExactly(reboundArg)
    }


    @Test
    fun testClearBindings() {
        val sql = "this is a SQL for test"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildSuccessfullyExecutingCompiledStatement(sql), storingLogger
        )

        // execute
        loggingCompiledStatement
                .bindInt(1, 5)
                .bindString(2, "test 6")
                .clearBindings()
                .execute()

        // assert info passed to logger
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).isEmpty()
    }


    @Test
    fun testExecutingMultipleTimes() {
        val sql = "this is a SQL for test"
        val argForFirstExecution = "test arg 1"
        val argForSecondExecution = "test arg 2"

        val storingLogger = StoringLogger()
        val loggingCompiledStatement = LoggingCompiledStatement(
                buildSuccessfullyExecutingCompiledStatement(sql), storingLogger
        )

        // bind & execute
        loggingCompiledStatement.bindString(1, argForFirstExecution).execute()

        // assert info passed to logger
        assertThat(storingLogger.onExecutionFailed).isNull()
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).containsOnly(
                argForFirstExecution
        )

        // bind & execute again
        loggingCompiledStatement.bindString(1, argForSecondExecution).execute()

        // assert info passed to logger
        assertThat(storingLogger.onExecutionFailed).isNull()
        assertThat(storingLogger.onSuccessfulExecution?.sql).isEqualTo(sql)
        assertThat(storingLogger.onSuccessfulExecution?.arguments).containsOnly(
                argForSecondExecution
        )
    }


    companion object {

        private fun buildSuccessfullyExecutingCompiledStatement(sql: String):
                CompiledStatement<Int> {

            val compiledStatementMock = buildMockCompiledStatement(sql)
            whenever(compiledStatementMock.execute()).thenReturn(0)
            return compiledStatementMock
        }


        private fun buildCompiledStatementThatThrowsOnExecuting(sql: String):
                CompiledStatement<Int> {

            val compiledStatementMock = buildMockCompiledStatement(sql)
            whenever(compiledStatementMock.execute()).thenThrow(RuntimeException::class.java)
            return compiledStatementMock
        }


        private fun buildMockCompiledStatement(sql: String): CompiledStatement<Int> {
            val compiledStatementMock = mock<CompiledStatement<Int>>()
            whenever(compiledStatementMock.sql).thenReturn(sql)
            return compiledStatementMock
        }
    }
}