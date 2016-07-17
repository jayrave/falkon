package com.jayrave.falkon.mapper

interface EnhancedColumn<in T : Any, C> : Column<T, C> {

    /**
     * Optional max length of the data in this column. The length should be dependent on
     * the [dbType] of this column
     */
    val maxSize: Int?

    /**
     * Whether this column allows NULL values
     */
    val isNonNull: Boolean
}