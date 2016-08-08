package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.*

import org.junit.Test

class EventReportingCompiledUpdateTest {

    private val tableName = "example table name"

    @Test
    fun testEventIsReportedIfAtLeastOneRecordIsUpdated() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledUpdate(buildCompiledUpdate(1), onExecuteWithEffects)
        assertThat(onExecuteWithEffectsCalled).isTrue()
    }


    @Test
    fun testEventIsNotReportedIfNoRecordIsUpdated() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledUpdate(buildCompiledUpdate(0), onExecuteWithEffects)
        assertThat(onExecuteWithEffectsCalled).isFalse()
    }


    private fun buildCompiledUpdate(numberOfRowsUpdated: Int): CompiledUpdate {
        val compiledUpdateMock = mock<CompiledUpdate>()
        whenever(compiledUpdateMock.execute()).thenReturn(numberOfRowsUpdated)
        return compiledUpdateMock
    }


    private fun buildAndExecuteEventReportingCompiledUpdate(
            compiledUpdate: CompiledUpdate, onExecuteWithEffects: (DbEvent) -> Unit) {

        EventReportingCompiledUpdate(tableName, compiledUpdate, onExecuteWithEffects).execute()
    }
}