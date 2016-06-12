package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.dao.lib.LinkedHashMapBackedIterable
import com.jayrave.falkon.engine.CompiledInsert
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.compileInsert

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

        override fun build(): CompiledInsert {
            val map = dataConsumer.map
            return table.configuration.engine
                    .compileInsert(table.name, LinkedHashMapBackedIterable.forKeys(map))
                    .bindAll(LinkedHashMapBackedIterable.forValues(map))
        }
    }
}