package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao

/**
 * A enhanced version of [Table] that provides a DAO and also knows to build the
 * CREATE TABLE SQL for itself
 */
interface EnhancedTable<T : Any, ID : Any, out D : Dao<T, ID>> : Table<T, ID> {

    override val idColumn: EnhancedColumn<T, ID>
    override val allColumns: Set<EnhancedColumn<T, *>>

    /**
     * DAO associated with this table
     */
    val dao: D

    /**
     * When called, implementations should build a CREATE TABLE SQL statement
     * for this table & return it
     */
    fun buildCreateTableSql(): String
}