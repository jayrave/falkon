package com.jayrave.falkon.dao.lib

/**
 * To expose items from an iterable in a different form
 */
internal class IterableBackedIterable<T, R>(
        private val iterable: Iterable<T>,
        private val transformer: (T) -> R) : Iterable<R> {

    override fun iterator(): Iterator<R> = IterableBackedIterator()


    private inner class IterableBackedIterator : Iterator<R> {

        private val iterator by lazy { iterable.iterator() }

        override fun hasNext() = iterator.hasNext()
        override fun next(): R = transformer.invoke(iterator.next())
    }
}