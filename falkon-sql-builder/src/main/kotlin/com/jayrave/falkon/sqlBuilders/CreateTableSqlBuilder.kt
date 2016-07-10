package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.TableInfo

interface CreateTableSqlBuilder {

    /**
     * @param [tableInfo] the info from which
     */
    fun build(tableInfo: TableInfo): String
}

