package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.engine.safeCloseAfterExecution
import com.jayrave.falkon.iterables.IterableBackedIterable
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder

internal class InsertBuilderImpl<T : Any>(
        override val table: Table<T, *>, private val insertSqlBuilder: InsertSqlBuilder) :
        InsertBuilder<T> {

    private val dataConsumer = LinkedHashMapBackedDataConsumer()

    /**
     * Calling this method again for columns that have been already set will overwrite the
     * existing values for those columns
     */
    override fun values(setter: InnerSetter<T>.() -> Any?): Ender {
        InnerSetterImpl().setter()
        return EnderImpl()
    }


    private inner class EnderImpl : Ender {

        override fun build(): Insert {
            val map = dataConsumer.map
            val sql = insertSqlBuilder.build(table.name, IterableBackedIterable.create(map.keys))
            return InsertImpl(table.name, sql, IterableBackedIterable.create(map.values))
        }

        override fun compile(): CompiledStatement<Int> {
            val insert = build()
            return table.configuration.engine
                    .compileInsert(table.name, insert.sql)
                    .closeIfOpThrows { bindAll(insert.arguments) }
        }

        override fun insert(): Boolean {
            return compile().safeCloseAfterExecution() >= 1
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