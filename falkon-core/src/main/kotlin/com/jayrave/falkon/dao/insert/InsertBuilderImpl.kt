package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.Column
import com.jayrave.falkon.SinkBackedDataConsumer
import com.jayrave.falkon.Table
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

internal class InsertBuilderImpl<T : Any, E : Engine<S>, S : Sink>(override val table: Table<T, *, E, S>) :
        InsertBuilder<T, E, S> {

    private val dataConsumer = SinkBackedDataConsumer(table.configuration.engine.sinkFactory.create())

    override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
        return AdderOrEnderImpl().set(column, value)
    }


    private inner class AdderOrEnderImpl : AdderOrEnder<T> {

        override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
            dataConsumer.setColumnName(column.name)
            column.putStorageFormIn(value, dataConsumer)
            return this
        }

        override fun insert(): Long {
            return table.configuration.engine.insert(table.name, dataConsumer.sink)
        }
    }
}