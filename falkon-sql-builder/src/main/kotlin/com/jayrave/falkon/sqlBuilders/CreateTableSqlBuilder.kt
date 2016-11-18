package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.TableInfo

interface CreateTableSqlBuilder {

    /**
     * Builds CREATE TABLE... statement. Sometimes multiple statements may have to be executed.
     * For eg., in some databases creating a sequence may involve running a statement & another
     * statement for the actual table creation statement
     *
     * *NOTE:* If multiple statements are returned, they should be executed atomically i.e.,
     * either all the statements must be executed or none of them should be
     *
     * @param [tableInfo] the info from which the statement has to be built
     */
    fun build(tableInfo: TableInfo): List<String>
}

