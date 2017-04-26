package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.DefaultEngine
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.test.TestSourceClosure
import com.jayrave.falkon.engine.test.TestSourceGetFieldValueCalls
import com.jayrave.falkon.engine.test.TestSourceMoveCalls
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CursorBackedSourceTest : BaseClassForIntegrationTests() {

    private lateinit var engine: Engine

    @Before
    fun setUpResultSetBackedSourceTest() {
        engine = DefaultEngine(engineCore)
    }

    @Test
    fun `is backtrackable`() {
        assertThat(CursorBackedSource(mock()).canBacktrack).isTrue()
    }

    @Test
    fun `#isClosed returns appropriate flag`() {
        TestSourceClosure(engine).`#isClosed returns appropriate flag`()
    }

    @Test
    fun `working on source after closing it throws`() {
        TestSourceClosure(engine).`working on source after closing it throws`()
    }

    @Test
    fun `source starts before first row & #moveToNext can go all the way to just after the last row`() {
        TestSourceMoveCalls(engine).`source starts before first row & #moveToNext can go all the way to just after the last row`()
    }

    @Test
    fun `#moveToPrevious can go all the way to just before the first row`() {
        TestSourceMoveCalls(engine).`#moveToPrevious can go all the way to just before the first row`()
    }

    @Test
    fun `#moveToNext & #moveToPrevious do not go beyond one invalid row`() {
        TestSourceMoveCalls(engine).`#moveToNext & #moveToPrevious do not go beyond one invalid row`()
    }

    @Test
    fun `#getColumnIndex throws for column name not in the result set`() {
        TestSourceGetFieldValueCalls(engine).`#getColumnIndex throws for column name not in the result set`()
    }

    @Test
    fun `test #getColumnIndex & #get* Calls`() {
        TestSourceGetFieldValueCalls(engine).`test #getColumnIndex & #get* Calls`()
    }

    @Test
    fun `test #getColumnIndex & #isNull Calls`() {
        TestSourceGetFieldValueCalls(engine).`test #getColumnIndex & #isNull Calls`()
    }
}