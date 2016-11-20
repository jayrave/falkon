package com.jayrave.falkon.sqlBuilders.lib

/**
 * To map from one index to multiple indices
 */
interface IndexToIndicesMap {

    /**
     * Size of this map
     */
    val size: Int

    /**
     * @param [index] in [1, [size]]
     * @return [IntArray] that denotes all the indices this [index] maps to
     */
    fun indicesForIndex(index: Int): IntArray
}