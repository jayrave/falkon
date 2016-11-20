package com.jayrave.falkon.iterables

import org.assertj.core.api.Assertions.*

import org.junit.Test

class IterablesBackedIterableTest {

    @Test
    fun `for single empty iterable`() {
        assertThat(IterablesBackedIterable(listOf(emptyList<Int>()))).isEmpty()
    }


    @Test
    fun `for single non empty iterable`() {
        val inputList = listOf(1, 2, 3, 4, 5)
        val iterable = IterablesBackedIterable(listOf(inputList))

        val expectedIterator = inputList.iterator()
        val actualIterator = iterable.iterator()

        while (expectedIterator.hasNext()) {
            assertThat(expectedIterator.next()).isEqualTo(actualIterator.next())
        }

        assertThat(expectedIterator).isEmpty()
        assertThat(actualIterator).isEmpty()
    }


    @Test
    fun `for multiple non empty iterables`() {
        val inputList1 = listOf(1, 2, 3, 4, 5)
        val inputList2 = listOf(6, 7, 8)
        val iterable = IterablesBackedIterable(listOf(inputList1, inputList2))

        val expectedIterator = (inputList1 + inputList2).iterator()
        val actualIterator = iterable.iterator()

        while (expectedIterator.hasNext()) {
            assertThat(expectedIterator.next()).isEqualTo(actualIterator.next())
        }

        assertThat(expectedIterator).isEmpty()
        assertThat(actualIterator).isEmpty()
    }


    @Test
    fun `with both empty & non empty iterables`() {
        val inputList1 = listOf(1, 2, 3, 4, 5)
        val inputList2 = emptyList<Int>()
        val inputList3 = listOf(6, 7, 8)
        val inputList4 = emptyList<Int>()
        val iterable = IterablesBackedIterable(
                listOf(inputList1, inputList2, inputList3, inputList4)
        )

        val expectedIterator = (inputList1 + inputList3).iterator()
        val actualIterator = iterable.iterator()

        while (expectedIterator.hasNext()) {
            assertThat(expectedIterator.next()).isEqualTo(actualIterator.next())
        }

        assertThat(expectedIterator).isEmpty()
        assertThat(actualIterator).isEmpty()
    }
}