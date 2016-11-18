package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface DeleteSqlBuilder {

    /**
     * Builds a db specific statement similar to `DELETE FROM ...`
     *
     * @param [tableName] the table to delete from
     * @param [whereSections] A list of sections, applied in iteration order used to build the
     * optional SQL WHERE clause. Passing null denotes no WHERE in the built SQL
     */
    fun build(tableName: String, whereSections: Iterable<WhereSection>?): String
}