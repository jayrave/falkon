package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Engine
import org.assertj.core.api.Assertions.assertThat

class TestSourceClosure(engine: Engine) : BaseClassForTestingSourceImplementation(engine) {

    init {
        insertRecord(5)
    }


    fun `#isClosed returns appropriate flag`() {
        val cs = engine.compileQuery(listOf(TABLE_NAME), SELECT_ALL_SQL)
        val source = cs.execute()

        assertThat(source.isClosed).isFalse()
        assertThat(source.isClosed).isFalse() // Stays the same
        source.close() // Close source
        assertThat(source.isClosed).isTrue()
        assertThat(source.isClosed).isTrue() // Stays the same

        cs.close() // Close compiled statement
    }


    fun `working on source after closing it throws`() {
        val cs = engine.compileQuery(listOf(TABLE_NAME), SELECT_ALL_SQL)
        val source = cs.execute()

        assertThat(source.moveToNext()).isTrue()
        assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isNotNull()
        source.close() // Close source

        failIfOpDoesNotThrow { source.getInt(source.getColumnIndex(INT_COLUMN_NAME)) }
        cs.close()
    }


    fun `working on source after closing compiled statement throws`() {
        val cs = engine.compileQuery(listOf(TABLE_NAME), SELECT_ALL_SQL)
        val source = cs.execute()

        assertThat(source.moveToNext()).isTrue()
        assertThat(source.getInt(source.getColumnIndex(INT_COLUMN_NAME))).isNotNull()
        cs.close() // Close compiled statement & not source

        failIfOpDoesNotThrow { source.getInt(source.getColumnIndex(INT_COLUMN_NAME)) }
    }
}