package com.jayrave.falkon.dao.update

import com.jayrave.falkon.Column
import com.jayrave.falkon.SinkBackedDataConsumer
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

internal class UpdateBuilderImpl<T : Any, E : Engine<S>, S : Sink>(override val table: Table<T, *, E, S>) :
        UpdateBuilder<T, E, S> {

    private val dataConsumer = SinkBackedDataConsumer(table.configuration.engine.sinkFactory.create())
    private var whereBuilder: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>? = null

    override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
        return AdderOrEnderImpl().set(column, value)
    }

    private fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
        whereBuilder = WhereBuilderImpl { PredicateAdderOrEnderImpl(it) }
        return whereBuilder!!
    }

    private fun update(): Int {
        val where: Where? = whereBuilder?.build()
        return table.configuration.engine.update(table.name, dataConsumer.sink, where?.clause, where?.arguments)
    }


    private inner class AdderOrEnderImpl : AdderOrEnder<T> {

        override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
            dataConsumer.setColumnName(column.name)
            column.putStorageFormIn(value, dataConsumer)
            return this
        }

        override fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
            return this@UpdateBuilderImpl.where()
        }

        override fun update(): Int {
            return this@UpdateBuilderImpl.update()
        }
    }


    private inner class PredicateAdderOrEnderImpl(private val delegate: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>) :
            PredicateAdderOrEnder<T> {

        override fun and(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            delegate.and()
            return delegate
        }

        override fun or(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            delegate.or()
            return delegate
        }

        override fun update(): Int {
            return this@UpdateBuilderImpl.update()
        }
    }
}