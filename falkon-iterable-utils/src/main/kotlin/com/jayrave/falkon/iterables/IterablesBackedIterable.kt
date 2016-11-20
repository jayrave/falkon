package com.jayrave.falkon.iterables

/**
 * To expose multiple iterables as a single continuous iterable
 */
class IterablesBackedIterable<out T>(private val iterables: List<Iterable<T>>) : Iterable<T> {

    override fun iterator(): Iterator<T> = IterablesBackedIterator()


    private inner class IterablesBackedIterator : Iterator<T> {

        private var iterator = emptyList<T>().iterator()
        private var currentSectionIndex = -1

        override fun hasNext(): Boolean {
            var hasNext = iterator.hasNext()
            while (!hasNext && currentSectionIndex < iterables.size - 1) {
                // This iterable has run out. Move on to the next one
                iterator = iterables[++currentSectionIndex].iterator()
                hasNext = iterator.hasNext()
            }

            return hasNext
        }

        override fun next(): T {
            hasNext() // To move on to the next iterator if required
            return iterator.next()
        }
    }
}