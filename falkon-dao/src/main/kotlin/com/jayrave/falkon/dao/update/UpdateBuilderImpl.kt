package com.jayrave.falkon.dao.update

import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.engine.safeCloseAfterExecution
import com.jayrave.falkon.iterables.IterableBackedIterable
import com.jayrave.falkon.iterables.IterablesBackedIterable
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.dao.where.AdderOrEnder as WhereAdderOrEnder

internal class UpdateBuilderImpl<T : Any>(
        override val table: Table<T, *>, private val updateSqlBuilder: UpdateSqlBuilder) :
        UpdateBuilder<T> {

    private val dataConsumer = LinkedHashMapBackedDataConsumer()
    private var whereBuilder: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>? = null

    /**
     * Calling this method again for columns that have been already set will overwrite the
     * existing values for those columns
     */
    override fun values(setter: InnerSetter<T>.() -> Any?): AdderOrEnder<T> {
        InnerSetterImpl().setter()
        return AdderOrEnderImpl()
    }

    private fun build(): Update {
        val map = dataConsumer.map
        val where: Where? = whereBuilder?.build()
        val columns: Iterable<String> = IterableBackedIterable.create(map.keys)

        val sql = updateSqlBuilder.build(table.name, columns, where?.whereSections)
        val arguments = IterablesBackedIterable(listOf(
                IterableBackedIterable.create(map.values),
                where?.arguments ?: emptyList()
        ))

        return UpdateImpl(table.name, sql, arguments)
    }

    private fun compile(): CompiledStatement<Int> {
        val update = build()
        return table.configuration.engine
                .compileUpdate(table.name, update.sql)
                .closeIfOpThrows { bindAll(update.arguments) }
    }

    private fun update(): Int {
        return compile().safeCloseAfterExecution()
    }


    private inner class AdderOrEnderImpl : AdderOrEnder<T> {

        override fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
            whereBuilder = WhereBuilderImpl({ PredicateAdderOrEnderImpl(it) })
            return whereBuilder!!
        }

        override fun build(): Update {
            return this@UpdateBuilderImpl.build()
        }

        override fun compile(): CompiledStatement<Int> {
            return this@UpdateBuilderImpl.compile()
        }

        override fun update(): Int {
            return this@UpdateBuilderImpl.update()
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

        override fun compile(): CompiledStatement<Int> {
            return this@UpdateBuilderImpl.compile()
        }

        override fun update(): Int {
            return this@UpdateBuilderImpl.update()
        }
    }


    private inner class InnerSetterImpl : InnerSetter<T> {

        /**
         * Calling this method again for a column that has been already set will overwrite the
         * existing value for that column
         */
        override fun <C> set(column: Column<T, C>, value: C) {
            dataConsumer.setColumnName(column.name)
            column.putStorageFormIn(value, dataConsumer)
        }
    }
}