package com.jayrave.falkon.dao.where

import com.jayrave.falkon.dao.lib.IterableBackedIterable
import java.util.*

/**
 * To expose items from a list in a different form
 */
internal class ListBackedList<T, R>(
        private val list: List<T>,
        private val transformer: (T) -> R) : List<R> {

    override val size: Int
        get() = list.size

    override fun contains(element: R): Boolean {
        return indexOf(element) >= 0
    }

    override fun containsAll(elements: Collection<R>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int): R {
        return transformer.invoke(list[index])
    }

    override fun indexOf(element: R): Int {
        return list.indexOfFirst { element == transformer.invoke(it) }
    }

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun iterator(): Iterator<R> {
        return IterableBackedIterable(list, transformer).iterator()
    }

    override fun lastIndexOf(element: R): Int {
        return list.indexOfLast { element == transformer.invoke(it) }
    }

    override fun listIterator(): ListIterator<R> {
        return ListIteratorBackedListIterator(list.listIterator(), transformer)
    }

    override fun listIterator(index: Int): ListIterator<R> {
        return ListIteratorBackedListIterator(list.listIterator(index), transformer)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<R> {
        val result = ArrayList<R>(toIndex - fromIndex)
        for (i in fromIndex..toIndex - 1) {
            result.add(transformer.invoke(list[i]))
        }

        return result
    }


    private class ListIteratorBackedListIterator<T, R>(
            private val listIterator: ListIterator<T>,
            private val transformer: (T) -> R) : ListIterator<R> {

        override fun hasNext(): Boolean = listIterator.hasNext()
        override fun hasPrevious(): Boolean = listIterator.hasPrevious()
        override fun next(): R = transformer.invoke(listIterator.next())
        override fun nextIndex(): Int = listIterator.nextIndex()
        override fun previous(): R = transformer.invoke(listIterator.previous())
        override fun previousIndex(): Int = listIterator.previousIndex()
    }
}