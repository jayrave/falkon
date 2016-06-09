package com.jayrave.falkon.dao.lib

import java.util.*

/**
 * Since this [Iterable] is backed by [LinkedHashMap], the iteration order is deterministic
 * and follows that of the map
 */
internal class LinkedHashMapBackedIterable<K, V, T>(
        private val linkedHashMap: LinkedHashMap<K, V>,
        private val extractor: (Map.Entry<K,V>) -> T) : Iterable<T> {

    override fun iterator(): Iterator<T> = LinkedHashMapBackedIterator()


    private inner class LinkedHashMapBackedIterator : Iterator<T> {

        private val iterator by lazy { linkedHashMap.iterator() }

        override fun hasNext() = iterator.hasNext()
        override fun next(): T = extractor.invoke(iterator.next())
    }


    companion object {

        fun <K, V> forKeys(map: LinkedHashMap<K, V>): LinkedHashMapBackedIterable<K, V, K> {
            return LinkedHashMapBackedIterable(map) { it.key }
        }

        fun <K, V> forValues(map: LinkedHashMap<K, V>): LinkedHashMapBackedIterable<K, V, V> {
            return LinkedHashMapBackedIterable(map) { it.value }
        }
    }
}