package com.jayrave.falkon.dao.update

import com.jayrave.falkon.dao.lib.IterablesBackedIterable
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedIterable
import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledUpdate
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.dao.where.AdderOrEnder as WhereAdderOrEnder

internal class UpdateBuilderImpl<T : Any>(
        override val table: Table<T, *>, private val updateSqlBuilder: UpdateSqlBuilder,
        private val argPlaceholder: String) : UpdateBuilder<T> {

    private val dataConsumer = LinkedHashMapBackedDataConsumer()
    private var whereBuilder: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>? = null

    override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
        return AdderOrEnderImpl().set(column, value)
    }

    private fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
        whereBuilder = WhereBuilderImpl { PredicateAdderOrEnderImpl(it) }
        return whereBuilder!!
    }

    private fun build(): Update {
        val map = dataConsumer.map
        val where: Where? = whereBuilder?.build()
        val columns: Iterable<String> = LinkedHashMapBackedIterable.forKeys(map)

        val sql = updateSqlBuilder.build(table.name, columns, where?.whereSections, argPlaceholder)
        val arguments = IterablesBackedIterable(listOf(
                LinkedHashMapBackedIterable.forValues(map),
                where?.arguments ?: emptyList()
        ))

        return UpdateImpl(sql, arguments)
    }

    private fun compile(): CompiledUpdate {
        val update = build()
        return table.configuration.engine
                .compileUpdate(update.sql)
                .closeIfOpThrows { bindAll(update.arguments) }
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

        override fun build(): Update {
            return this@UpdateBuilderImpl.build()
        }

        override fun compile(): CompiledUpdate {
            return this@UpdateBuilderImpl.compile()
        }
    }


    private inner class PredicateAdderOrEnderImpl(
            private val delegate: WhereAdderOrEnder<T, PredicateAdderOrEnder<T>>) :
            PredicateAdderOrEnder<T> {

        override fun and(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            return delegate.and()
        }

        override fun or(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            return delegate.or()
        }

        override fun build(): Update {
            return this@UpdateBuilderImpl.build()
        }

        override fun compile(): CompiledUpdate {
            return this@UpdateBuilderImpl.compile()
        }
    }
}