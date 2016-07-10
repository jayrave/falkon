package com.jayrave.falkon.sqlBuilders.lib

interface ColumnInfo {

    /**
     * Name of this column
     */
    val name: String

    /**
     * Type of this column
     */
    val type: String

    /**
     * Whether this column allows NULL values
     */
    val isNonNull: Boolean
}