package com.jayrave.falkon.iterables

/**
 * To expose items from an iterable in a different form
 */
class IterableBackedIterable<T, out R>(
        private val iterable: Iterable<T>,
        private val transformer: (T) -> R) : Iterable<R> {

    override fun iterator(): Iterator<R> = IteratorBackedIterator()


    private inner class IteratorBackedIterator : Iterator<R> {

        private val iterator by lazy { iterable.iterator() }

        override fun hasNext() = iterator.hasNext()
        override fun next(): R = transformer.invoke(iterator.next())
    }
}