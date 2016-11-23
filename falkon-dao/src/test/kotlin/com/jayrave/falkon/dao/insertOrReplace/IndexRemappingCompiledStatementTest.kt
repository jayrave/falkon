package com.jayrave.falkon.dao.insertOrReplace

import com.jayrave.falkon.dao.testLib.IntReturningOneShotCompiledStatementForTest
import com.jayrave.falkon.engine.Type
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.*
import org.junit.Test

class IndexRemappingCompiledStatementTest {

    @Test
    fun `indices are mapped`() {
        val delegate = IntReturningOneShotCompiledStatementForTest("test", "dummy_sql")
        val indexMap = intArrayOf(Int.MIN_VALUE, 2, 4, 6, 8, 10, 12, 14, 16)

        // Create compiled statement & bind values
        val indexRemappingCs = IndexRemappingCompiledStatement(delegate, indexMap)
        indexRemappingCs.bindShort(1, 5)
        indexRemappingCs.bindInt(2, 6)
        indexRemappingCs.bindLong(3, 7)
        indexRemappingCs.bindFloat(4, 8F)
        indexRemappingCs.bindDouble(5, 9.0)
        indexRemappingCs.bindString(6, "test 10")
        indexRemappingCs.bindBlob(7, byteArrayOf(11))
        indexRemappingCs.bindNull(8, Type.SHORT)

        // Assert that bindings were redirected to appropriate indices
        assertThat(delegate.shortBoundAt(2)).isEqualTo(5)
        assertThat(delegate.intBoundAt(4)).isEqualTo(6)
        assertThat(delegate.longBoundAt(6)).isEqualTo(7)
        assertThat(delegate.floatBoundAt(8)).isEqualTo(8F)
        assertThat(delegate.doubleBoundAt(10)).isEqualTo(9.0)
        assertThat(delegate.stringBoundAt(12)).isEqualTo("test 10")
        assertThat(delegate.blobBoundAt(14)).isEqualTo(byteArrayOf(11))
        assertThat(delegate.isNullBoundAt(16)).isTrue()
    }


    @Test
    fun `throws for inappropriate indices`() {

        fun `local throws for inappropriate indices`(indexMap: IntArray, inappropriateIndex: Int) {
            val indexRemappingCs = IndexRemappingCompiledStatement(mock(), indexMap)
            val exceptionWasThrown = try {
                indexRemappingCs.bindShort(inappropriateIndex, 5)
                false
            } catch (e: ArrayIndexOutOfBoundsException) {
                true
            }

            if (!exceptionWasThrown) {
                failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException::class.java)
            }
        }

        `local throws for inappropriate indices`(intArrayOf(), 0)
        `local throws for inappropriate indices`(intArrayOf(1), 0)
        `local throws for inappropriate indices`(intArrayOf(1), 1)
        `local throws for inappropriate indices`(intArrayOf(2), 0)
        `local throws for inappropriate indices`(intArrayOf(2), 2)
    }
}