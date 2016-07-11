package com.jayrave.falkon.sqlBuilders.lib

interface ColumnInfo {

    /**
     * Name of this column
     */
    val name: String

    /**
     * Type of this column
     */
    val dataType: String

    /**
     * Optional max length of the data in this column
     */
    val maxSize: Int?

    /**
     * Whether this column allows NULL values
     */
    val isNonNull: Boolean
}