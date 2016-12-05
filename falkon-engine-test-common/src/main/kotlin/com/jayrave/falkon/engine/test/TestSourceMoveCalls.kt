package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Source
import org.assertj.core.api.Assertions.assertThat

class TestSourceMoveCalls(engine: Engine) : BaseClassForTestingSourceImplementation(engine) {

    fun `source starts before first row & #moveToNext can go all the way to just after the last row`() {
        val numberOfRows = 5
        (1..numberOfRows).forEach {
            insertRecord(it)
        }

        queryForAll { source ->
            var moveCount = 0
            while (source.moveToNext()) {
                moveCount++
                assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(
                        moveCount
                )
            }

            // Last iteration would have taken the pointer to one after the last row
            assertThat(moveCount).isEqualTo(numberOfRows)

            // Since source is pointing to an invalid row now, it should throw
            failIfOpDoesNotThrow { source.getInt(source.getColumnIndex(INT_COLUMN_NAME)) }
        }
    }


    fun `#moveToPrevious can go all the way to just before the first row`() {
        val numberOfRows = 5
        (1..numberOfRows).forEach {
            insertRecord(it)
        }

        queryForAll { source ->

            // Go till past the last row
            while (source.moveToNext()) {}

            var moveCountDown = numberOfRows
            while (source.moveToPrevious()) {
                assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isEqualTo(
                        moveCountDown
                )

                moveCountDown--
            }

            // Last iteration would have taken the pointer to one before the first row
            assertThat(moveCountDown).isEqualTo(0)

            // Since source is pointing to an invalid row now, it should throw
            failIfOpDoesNotThrow { source.getInt(source.getColumnIndex(INT_COLUMN_NAME)) }
        }
    }


    fun `#moveToNext & #moveToPrevious do not go beyond one invalid row`() {
        insertRecord(seed = 42)
        queryForAll { source ->
            assertThat(source.moveToNext()).isTrue()
            assertThat(source.moveToNext()).isFalse() // Invalid row
            assertThat(source.moveToNext()).isFalse() // Invalid row
            assertThat(source.moveToNext()).isFalse() // Invalid row

            // Back to valid row (even though move to next was called more times)
            assertThat(source.moveToPrevious()).isTrue()
            assertThat(source.moveToPrevious()).isFalse() // Invalid row
            assertThat(source.moveToPrevious()).isFalse() // Invalid row
            assertThat(source.moveToPrevious()).isFalse() // Invalid row

            // Back to valid row (even though move to previous was called more times)
            assertThat(source.moveToNext()).isTrue()
        }
    }


    private fun queryForAll(test: (Source) -> Any?) {
        val cs = engine.compileQuery(listOf(TABLE_NAME), SELECT_ALL_SQL)
        val source = cs.execute()
        try {
            test.invoke(source)
        } finally {
            source.close()
            cs.close()
        }
    }
}