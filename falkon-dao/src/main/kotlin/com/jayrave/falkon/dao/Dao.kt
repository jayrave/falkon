package com.jayrave.falkon.dao

import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.delete.DeleteBuilder
import com.jayrave.falkon.dao.insert.InsertBuilder
import com.jayrave.falkon.dao.query.QueryBuilder
import com.jayrave.falkon.dao.update.UpdateBuilder

interface Dao<T : Any, ID : Any> {

    val table: Table<T, ID>

    fun insertBuilder(): InsertBuilder<T>
    fun updateBuilder(): UpdateBuilder<T>
    fun deleteBuilder(): DeleteBuilder<T>
    fun queryBuilder(): QueryBuilder<T>
}