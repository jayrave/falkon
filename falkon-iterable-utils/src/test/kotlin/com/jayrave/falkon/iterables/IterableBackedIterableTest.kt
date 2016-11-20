package com.jayrave.falkon.iterables

import org.assertj.core.api.Assertions.*

import org.junit.Test

class IterableBackedIterableTest {

    @Test
    fun `for empty iterable`() {
        assertThat(IterableBackedIterable.create(emptyList<Int>())).isEmpty()
    }


    @Test
    fun `for non empty iterable`() {
        val intIterable = listOf(1, 2, 3, 4, 5)
        val stringIterable = intIterable.map(Int::toString)
        val intToStringIterable = IterableBackedIterable.create(intIterable, Int::toString)

        val stringIterator = stringIterable.iterator()
        val intToStringIterator = intToStringIterable.iterator()

        while (stringIterator.hasNext()) {
            assertThat(stringIterator.next()).isEqualTo(intToStringIterator.next())
        }

        assertThat(stringIterator.hasNext()).isFalse()
        assertThat(intToStringIterator.hasNext()).isFalse()
    }
}