package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.*

import org.junit.Test

class EventReportingCompiledInsertTest {

    private val tableName = "example table name"

    @Test
    fun testEventIsReportedIfAtLeastOneRecordIsInserted() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledInsert(buildCompiledInsert(1), onExecuteWithEffects)
        assertThat(onExecuteWithEffectsCalled).isTrue()
    }


    @Test
    fun testEventIsNotReportedIfNoRecordIsInserted() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledInsert(buildCompiledInsert(0), onExecuteWithEffects)
        assertThat(onExecuteWithEffectsCalled).isFalse()
    }


    private fun buildCompiledInsert(numberOfRowsInserted: Int): CompiledInsert {
        val compiledInsertMock = mock<CompiledInsert>()
        whenever(compiledInsertMock.execute()).thenReturn(numberOfRowsInserted)
        return compiledInsertMock
    }


    private fun buildAndExecuteEventReportingCompiledInsert(
            compiledInsert: CompiledInsert, onExecuteWithEffects: (DbEvent) -> Unit) {

        EventReportingCompiledInsert(tableName, compiledInsert, onExecuteWithEffects).execute()
    }
}