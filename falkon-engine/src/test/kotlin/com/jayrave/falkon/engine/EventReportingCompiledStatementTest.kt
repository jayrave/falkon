package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.*

import org.junit.Test
import java.util.*

class EventReportingCompiledStatementTest {

    private val tableName = "example table name"

    @Test
    fun testEventIsReportedIfAtLeastOneRowIsAffected() {
        val dbEvents = ArrayList<DbEvent>()
        val onExecuteWithEffects = { dbEvent: DbEvent ->
            dbEvents.add(dbEvent)
            Unit
        }

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(1), DbEvent.Type.INSERT, onExecuteWithEffects
        )

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(1), DbEvent.Type.UPDATE, onExecuteWithEffects
        )

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(1), DbEvent.Type.DELETE, onExecuteWithEffects
        )

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(1), DbEvent.Type.INSERT_OR_REPLACE, onExecuteWithEffects
        )

        assertThat(dbEvents).containsExactly(
                DbEvent.forInsert(tableName),
                DbEvent.forUpdate(tableName),
                DbEvent.forDelete(tableName),
                DbEvent.forInsertOrReplace(tableName)
        )
    }


    @Test
    fun testEventIsNotReportedIfNoRowsAreAffected() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(0), DbEvent.Type.INSERT, onExecuteWithEffects
        )

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(0), DbEvent.Type.UPDATE, onExecuteWithEffects
        )

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(0), DbEvent.Type.DELETE, onExecuteWithEffects
        )

        buildAndExecuteEventReportingCompiledStatement(
                buildCompiledStatement(0), DbEvent.Type.INSERT_OR_REPLACE, onExecuteWithEffects
        )

        assertThat(onExecuteWithEffectsCalled).isFalse()
    }


    private fun buildCompiledStatement(numberOfRowsAffected: Int): CompiledStatement<Int> {
        val compiledStatementMock = mock<CompiledStatement<Int>>()
        whenever(compiledStatementMock.execute()).thenReturn(numberOfRowsAffected)
        return compiledStatementMock
    }


    private fun buildAndExecuteEventReportingCompiledStatement(
            compiledStatement: CompiledStatement<Int>, eventType: DbEvent.Type,
            onExecuteWithEffects: (DbEvent) -> Unit) {

        EventReportingCompiledStatement(
                tableName, eventType, compiledStatement, onExecuteWithEffects
        ).execute()
    }
}