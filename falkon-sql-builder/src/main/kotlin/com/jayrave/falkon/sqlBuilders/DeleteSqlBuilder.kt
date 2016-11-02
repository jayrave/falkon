package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface DeleteSqlBuilder {

    /**
     * Builds a db specific statement similar to `DELETE FROM ...`
     *
     * @param [tableName] the table to delete from
     * @param [whereSections] A list of sections, applied in iteration order used to build the
     * optional SQL WHERE clause. Passing null denotes no WHERE in the built SQL
     * @param [argPlaceholder] to use as placeholders to prevent SQL injection
     */
    fun build(
            tableName: String,
            whereSections: Iterable<WhereSection>?,
            argPlaceholder: String
    ): String
}