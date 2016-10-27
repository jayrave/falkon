package com.jayrave.falkon.sqlBuilders

/**
 * To provide database specific information
 */
interface Dialect {

    /**
     * Something akin to MySQL's AUTO_INCREMENT so that if a value isn't explicitly assigned
     * for a field, an auto incremented value is used
     */
    val autoIncrementExpression: String
}