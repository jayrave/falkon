package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedIterable
import com.jayrave.falkon.engine.CompiledInsert
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows

internal class InsertBuilderImpl<T : Any>(override val table: Table<T, *, *>) : InsertBuilder<T> {

    private val dataConsumer = LinkedHashMapBackedDataConsumer()

    override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
        return AdderOrEnderImpl().set(column, value)
    }


    private inner class AdderOrEnderImpl : AdderOrEnder<T> {

        override fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T> {
            dataConsumer.setColumnName(column.name)
            column.putStorageFormIn(value, dataConsumer)
            return this
        }

        override fun build(): Insert {
            val map = dataConsumer.map
            val sql = table.configuration.engine.buildInsertSql(
                    table.name, LinkedHashMapBackedIterable.forKeys(map)
            )

            return Insert(sql, LinkedHashMapBackedIterable.forValues(map))
        }

        override fun compile(): CompiledInsert {
            val insert = build()
            return table.configuration.engine
                    .compileInsert(insert.sql)
                    .closeIfOpThrows { bindAll(insert.arguments) }
        }
    }
}