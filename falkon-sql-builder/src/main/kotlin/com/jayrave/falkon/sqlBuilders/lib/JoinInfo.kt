package com.jayrave.falkon.sqlBuilders.lib

interface JoinInfo {

    /**
     * The type of join this represents
     */
    val type: Type

    /**
     * The qualified column name from the first table
     */
    val qualifiedLocalColumnName: String

    /**
     * The name of the table that serves as the second table of the JOIN clause
     */
    val nameOfTableToJoin: String

    /**
     * The qualified column name from the second table
     */
    val qualifiedColumnNameFromTableToJoin: String


    enum class Type {
        INNER_JOIN,
        LEFT_OUTER_JOIN,
        RIGHT_OUTER_JOIN,
        FULL_OUTER_JOIN
    }
}