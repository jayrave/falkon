package com.jayrave.falkon.dao.delete

import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledDelete
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder

internal class DeleteBuilderImpl<T : Any>(
        override val table: Table<T, *>, private val deleteSqlBuilder: DeleteSqlBuilder,
        private val argPlaceholder: String) : DeleteBuilder<T> {

    private var whereBuilder: WhereBuilderImpl<T, AdderOrEnder<T>>? = null

    override fun where(): WhereBuilder<T, AdderOrEnder<T>> {
        whereBuilder = WhereBuilderImpl { AdderOrEnderImpl(it) }
        return whereBuilder!!
    }

    override fun build(): Delete {
        val where: Where? = whereBuilder?.build()
        return DeleteImpl(
                deleteSqlBuilder.build(table.name, where?.whereSections, argPlaceholder),
                where?.arguments ?: emptyList()
        )
    }

    override fun compile(): CompiledDelete {
        val delete = build()
        return table.configuration.engine
                .compileDelete(delete.sql)
                .closeIfOpThrows { bindAll(delete.arguments) }
    }


    private inner class AdderOrEnderImpl(
            private val delegate: WhereBuilderImpl<T, AdderOrEnder<T>>) :
            AdderOrEnder<T> {

        override fun and(): AfterSimpleConnectorAdder<T, AdderOrEnder<T>> {
            delegate.and()
            return delegate
        }

        override fun or(): AfterSimpleConnectorAdder<T, AdderOrEnder<T>> {
            delegate.or()
            return delegate
        }

        override fun build(): Delete {
            return this@DeleteBuilderImpl.build()
        }

        override fun compile(): CompiledDelete {
            return this@DeleteBuilderImpl.compile()
        }
    }
}