package com.jayrave.falkon.sqlBuilders.lib

interface ForeignKeyConstraint {

    /**
     * Name of the column for which the foreign key constrain has to be enabled
     */
    val columnName: String

    /**
     * Name of the foreign table to which [foreignColumnName] belongs
     */
    val foreignTableName: String

    /**
     * Name of the column in [foreignTableName] that this column references to
     */
    val foreignColumnName: String
}