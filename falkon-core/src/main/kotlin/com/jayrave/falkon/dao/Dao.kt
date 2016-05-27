package com.jayrave.falkon.dao

import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.delete.DeleteBuilder
import com.jayrave.falkon.dao.insert.InsertBuilder
import com.jayrave.falkon.dao.query.QueryBuilder
import com.jayrave.falkon.dao.update.UpdateBuilder
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

interface Dao<T : Any, ID : Any, E : Engine<S>, S : Sink> {

    val table: Table<T, ID, E, S>

    fun insertBuilder(): InsertBuilder<T, E, S>
    fun updateBuilder(): UpdateBuilder<T, E, S>
    fun deleteBuilder(): DeleteBuilder<T>
    fun queryBuilder(): QueryBuilder<T>
}