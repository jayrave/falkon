package com.jayrave.falkon.dao.update

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedIterable
import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.compileUpdate
import com.jayrave.falkon.engine.executeAndClose

internal class UpdateBuilderImpl<T : Any>(override val table: Table<T, *, *>) : UpdateBuilder<T> {

    private val dataConsumer = LinkedHashMapBackedDataConsumer()
    private var whereBuilder: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>? = null

    override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
        return AdderOrEnderImpl().set(column, value)
    }

    private fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
        whereBuilder = WhereBuilderImpl { PredicateAdderOrEnderImpl(it) }
        return whereBuilder!!
    }

    private fun update(): Int {
        val map = dataConsumer.map
        val where: Where? = whereBuilder?.build()
        return table.configuration.engine
                .compileUpdate(table.name, LinkedHashMapBackedIterable.forKeys(map), where?.clause)
                .bindAll(LinkedHashMapBackedIterable.forValues(map))
                .bindAll(where?.arguments, map.size + 1) // 1-based index
                .executeAndClose()
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


    private inner class PredicateAdderOrEnderImpl(
            private val delegate: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>) :
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