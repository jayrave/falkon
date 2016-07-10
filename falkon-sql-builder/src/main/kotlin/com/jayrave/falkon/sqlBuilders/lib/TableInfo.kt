package com.jayrave.falkon.sqlBuilders.lib

interface TableInfo {

    /**
     * Name of this table
     */
    val name: String

    /**
     * All the columns for this table
     */
    val columnInfos: Iterable<ColumnInfo>

    /**
     * Name of the column that is supposed to act as this table's primary key.
     * It is assumed that this column is included in [columnInfos]
     */
    val primaryKeyConstraint: String

    /**
     * Uniqueness constrains for this table. It is assumed that all the columns in this
     * constrains are included in [columnInfos]
     */
    val uniquenessConstraints: Iterable<Iterable<String>>

    /**
     * Foreign key constrains for this table. It is assumed that all the columns in this
     * constrains are included in [columnInfos]
     */
    val foreignKeyConstraints: Iterable<ForeignKeyConstraint>
}