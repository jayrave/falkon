package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.IndexToIndicesMap
import com.jayrave.falkon.sqlBuilders.lib.SqlAndIndexToIndicesMap

interface InsertSqlBuilder {

    /**
     * `True` if insert or replace functionality is supported; `false` otherwise
     */
    val isInsertOrReplaceSupported: Boolean


    /**
     * Builds a db specific statement similar to `INSERT INTO ...`
     *
     * @param [tableName] the table to insert into
     * @param [columns] a list of columns (applied in iteration order) for which values will
     * be bound later
     */
    fun build(tableName: String, columns: Iterable<String>): String


    /**
     * Builds SQL statements that does something along the following:
     *  - Try to insert the record
     *  - If the record already exists, all non-id columns are updated
     *
     * Not every database strictly sticks to this. Please take a look at the database
     * specific implementation to get to know the actual behavior
     *
     * Not all databases have support for this functionality. This can be checked via the
     * flag [isInsertOrReplaceSupported]
     *
     * *CAUTION:* This method could throw if [isInsertOrReplaceSupported] is `false`
     *
     * @param [tableName] the table to insert into or update in
     * @param [idColumns] a list of columns that form the primary key for this table
     * @param [nonIdColumns] a list of columns that aren't part of the primary key for this table
     *
     * @return [SqlAndIndexToIndicesMap] that corresponds to the built statement. Both
     * [idColumns] & [nonIdColumns] are applied in iteration order one after another.
     * For eg., if [idColumns]'s size is 5 & [nonIdColumns]'s size is 2, then the
     * indices 1, 2, 3, 4 & 5 correspond to [idColumns] and indices 6 & 7 correspond to
     * [nonIdColumns]. The value for column that corresponds to index 1 should be bound
     * to all the indices returned by [IndexToIndicesMap.indicesForIndex] for 1
     */
    fun buildInsertOrReplace(
            tableName: String, idColumns: Iterable<String>, nonIdColumns: Iterable<String>
    ): SqlAndIndexToIndicesMap
}