package com.jayrave.falkon.sqlBuilders

interface InsertSqlBuilder {

    /**
     * `True` if insert or replace functionality is supported; `false` otherwise
     */
    val isInsertOrReplaceSupported: Boolean


    /**
     * Builds `INSERT INTO ...` statement
     * @param [tableName] the table to insert into
     * @param [columns] a list of columns for which values will be bound later
     * @param [argPlaceholder] to use as placeholders to prevent SQL injection
     */
    fun build(
            tableName: String,
            columns: Iterable<String>,
            argPlaceholder: String
    ): String


    /**
     * Builds SQL statements that does something along the following:
     *  - Try to insert the record defined by the passed in arguments
     *  - If the record already exists, it is updated with the passed values
     *
     * Not every database strictly sticks to this. Some database could resort to updating
     * the record even on some other constraint failures like non-null. Please take a look
     * at the database specific implementation to get to know the actual behavior
     *
     * Not all databases have support for this functionality. This can be checked via the
     * flag [isInsertOrReplaceSupported]
     *
     * *NOTE:* If multiple statements are returned, they should be executed atomically
     * for the desired effect
     *
     * *CAUTION:* This method could throw if [isInsertOrReplaceSupported] is `false`
     *
     * @param [tableName] the table to insert into
     * @param [columns] a list of columns for which values will be bound later
     * @param [argPlaceholder] to use as placeholders to prevent SQL injection
     */
    fun buildInsertOrReplace(
            tableName: String,
            columns: Iterable<String>,
            argPlaceholder: String
    ): List<String>
}