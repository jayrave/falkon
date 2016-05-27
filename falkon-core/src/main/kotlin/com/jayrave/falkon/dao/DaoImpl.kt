package com.jayrave.falkon.dao

import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.delete.DeleteBuilder
import com.jayrave.falkon.dao.delete.DeleteBuilderImpl
import com.jayrave.falkon.dao.insert.InsertBuilder
import com.jayrave.falkon.dao.insert.InsertBuilderImpl
import com.jayrave.falkon.dao.query.QueryBuilder
import com.jayrave.falkon.dao.query.QueryBuilderImpl
import com.jayrave.falkon.dao.update.UpdateBuilder
import com.jayrave.falkon.dao.update.UpdateBuilderImpl
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

open class DaoImpl<T : Any, ID : Any, E : Engine<S>, S : Sink>(override val table: Table<T, ID, E, S>) :
        Dao<T, ID, E, S> {

    override final fun insertBuilder(): InsertBuilder<T, E, S> = InsertBuilderImpl(table)
    override final fun updateBuilder(): UpdateBuilder<T, E, S> = UpdateBuilderImpl(table)
    override final fun deleteBuilder(): DeleteBuilder<T>  = DeleteBuilderImpl(table)
    override final fun queryBuilder(): QueryBuilder<T> = QueryBuilderImpl(table)
}