package com.jayrave.falkon.sqlBuilders

interface InsertOrReplaceSqlBuilder {

    /**
     * Builds SQL statements that does something along the following:
     *  - Try to insert the record
     *  - If the record already exists, all non-id columns are updated
     *
     * Not every database strictly sticks to this. Please take a look at the database
     * specific implementation to get to know the actual behavior
     *
     * @param [tableName] the table to insert into or update in
     * @param [idColumns] a list of columns that form the primary key for this table
     * @param [nonIdColumns] a list of columns that aren't part of the primary key for this table
     */
    fun build(
            tableName: String, idColumns: Iterable<String>, nonIdColumns: Iterable<String>
    ): String
}