package com.jayrave.falkon.sqlBuilders.common

import org.assertj.core.api.Assertions.*

import org.junit.Test
import java.util.*

class SimpleIndexToIndicesMapTest {

    @Test
    fun `appropriate indices are mapped`() {
        val map = SimpleIndexToIndicesMap(3)
        assertThat(Arrays.equals(map.indicesForIndex(1), intArrayOf(1)))
        assertThat(Arrays.equals(map.indicesForIndex(2), intArrayOf(2)))
        assertThat(Arrays.equals(map.indicesForIndex(3), intArrayOf(3)))
    }


    @Test(expected = IndexOutOfBoundsException::class)
    fun `inappropriate indices result in exception 1`() {
        val map = SimpleIndexToIndicesMap(3)
        map.indicesForIndex(0)
    }


    @Test(expected = IndexOutOfBoundsException::class)
    fun `inappropriate indices result in exception 2`() {
        val map = SimpleIndexToIndicesMap(3)
        map.indicesForIndex(4)
    }
}