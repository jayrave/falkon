package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.Column
import com.jayrave.falkon.SinkBackedDataConsumer
import com.jayrave.falkon.Table
import com.jayrave.falkon.engine.Sink

internal class InsertBuilderImpl<T : Any, S : Sink>(override val table: Table<T, *, *, S>) : InsertBuilder<T, S> {

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