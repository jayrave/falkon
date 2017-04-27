package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.DefaultEngine
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.test.TestSourceClosure
import com.jayrave.falkon.engine.test.TestSourceGetFieldValueCalls
import com.jayrave.falkon.engine.test.TestSourceMoveCalls
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.ResultSet

/**
 * Just to make sure that the calls are forwarded to appropriate methods in [ResultSet] and
 * return values are appropriately returned
 */
class ResultSetBackedSourceTest : BaseClassForIntegrationTests() {

    @Test
    fun `backtrackability is dependent on the type of result set`() {
        val resultSetMock = mock<ResultSet>()
        whenever(resultSetMock.type).thenReturn(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.TYPE_FORWARD_ONLY,
                -1234
        )

        val source = ResultSetBackedSource(resultSetMock)
        assertThat(source.canBacktrack).isTrue() // scroll insensitive
        assertThat(source.canBacktrack).isTrue() // scroll sensitive
        assertThat(source.canBacktrack).isFalse() // forward only
        assertThat(source.canBacktrack).isTrue() // some random int
    }

    @Test
    fun `#moveToPrevious should not call ResultSet#previous if it can not backtrack`() {
        val resultSetMock = mock<ResultSet>()
        whenever(resultSetMock.type).thenReturn(ResultSet.TYPE_FORWARD_ONLY)

        val source = ResultSetBackedSource(resultSetMock)
        source.moveToPrevious()

        // Verify that ResultSet#moveToPrevious wasn't called as source can't backtrack
        verify(resultSetMock).type
        verifyNoMoreInteractions(resultSetMock)
    }

    @Test
    fun `#isClosed returns appropriate flag`() {
        TestSourceClosure(buildEngine(false)).`#isClosed returns appropriate flag`()
    }

    @Test
    fun `working on source after closing it throws`() {
        TestSourceClosure(buildEngine(false)).`working on source after closing it throws`()
    }

    @Test
    fun `working on source after closing compiled statement throws`() {
        TestSourceClosure(buildEngine(false)).`working on source after closing compiled statement throws`()
    }

    @Test
    fun `source starts before first row & #moveToNext can go all the way to just after the last row`() {
        TestSourceMoveCalls(buildEngine(false)).`source starts before first row & #moveToNext can go all the way to just after the last row`()
    }

    @Test
    fun `#moveToPrevious can go all the way to just before the first row if source can backtrack`() {
        TestSourceMoveCalls(buildEngine(true)).`#moveToPrevious can go all the way to just before the first row`()
    }

    @Test
    fun `#moveToNext & #moveToPrevious do not go beyond one invalid row if source can backtrack`() {
        TestSourceMoveCalls(buildEngine(true)).`#moveToNext & #moveToPrevious do not go beyond one invalid row`()
    }

    @Test
    fun `#getColumnIndex throws for column name not in the result set`() {
        TestSourceGetFieldValueCalls(buildEngine(false)).`#getColumnIndex throws for column name not in the result set`()
    }

    @Test
    fun `test #getColumnIndex & #get* Calls`() {
        TestSourceGetFieldValueCalls(buildEngine(false)).`test #getColumnIndex & #get* Calls`()
    }

    @Test
    fun `test #getColumnIndex & #isNull Calls`() {
        TestSourceGetFieldValueCalls(buildEngine(false)).`test #getColumnIndex & #isNull Calls`()
    }

    private fun buildEngine(canBacktrack: Boolean): Engine {
        return DefaultEngine(JdbcEngineCore(dataSource, canBacktrack))
    }
}