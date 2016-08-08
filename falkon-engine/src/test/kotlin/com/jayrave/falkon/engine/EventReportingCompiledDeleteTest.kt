package com.jayrave.falkon.engine

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.*

import org.junit.Test

class EventReportingCompiledDeleteTest {

    private val tableName = "example table name"

    @Test
    fun testEventIsReportedIfAtLeastOneRecordIsDeleted() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledDelete(buildCompiledDelete(1), onExecuteWithEffects)
        assertThat(onExecuteWithEffectsCalled).isTrue()
    }


    @Test
    fun testEventIsNotReportedIfNoRecordIsDeleted() {
        var onExecuteWithEffectsCalled = false
        val onExecuteWithEffects = { dbEvent: DbEvent -> onExecuteWithEffectsCalled = true }

        buildAndExecuteEventReportingCompiledDelete(buildCompiledDelete(0), onExecuteWithEffects)
        assertThat(onExecuteWithEffectsCalled).isFalse()
    }


    private fun buildCompiledDelete(numberOfRowsDeleted: Int): CompiledDelete {
        val compiledDeleteMock = mock<CompiledDelete>()
        whenever(compiledDeleteMock.execute()).thenReturn(numberOfRowsDeleted)
        return compiledDeleteMock
    }


    private fun buildAndExecuteEventReportingCompiledDelete(
            compiledDelete: CompiledDelete, onExecuteWithEffects: (DbEvent) -> Unit) {

        EventReportingCompiledDelete(tableName, compiledDelete, onExecuteWithEffects).execute()
    }
}