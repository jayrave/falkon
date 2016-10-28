package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao

/**
 * A enhanced version of [Table] that provides a DAO and also knows to build the
 * CREATE TABLE SQL for itself
 */
interface EnhancedTable<T : Any, ID : Any, out D : Dao<T, ID>> : Table<T, ID> {

    override val idColumn: EnhancedColumn<T, ID>
    override val allColumns: Collection<EnhancedColumn<T, *>>

    /**
     * DAO associated with this table
     */
    val dao: D

    /**
     * When called, implementations should build statements to create this table
     *
     * *NOTE:* If multiple statements are returned, they should be executed atomically i.e.,
     * either all the statements must be executed or none of them should be
     */
    fun buildCreateTableSql(): List<String>
}