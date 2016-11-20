package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.IndexToIndicesMap

/**
 * An implementation of [IndexToIndicesMap] that does straight up mapping for appropriate indices.
 * For indices in [1, size], intArrayOf(index) is returned. Other indices result in exceptions
 */
class SimpleIndexToIndicesMap(override val size: Int) : IndexToIndicesMap {

    @Suppress("ConvertLambdaToReference")
    private val indices = Array(size) { intArrayOf(it + 1) }

    override fun indicesForIndex(index: Int): IntArray {
        throwIfIndexIsOutOfBounds(index)
        val indices = indices[index - 1]
        return indices
    }


    private fun throwIfIndexIsOutOfBounds(index: Int) {
        if (index <= 0 || index > size) {
            throw IndexOutOfBoundsException("Index should be in [1, $size]")
        }
    }
}