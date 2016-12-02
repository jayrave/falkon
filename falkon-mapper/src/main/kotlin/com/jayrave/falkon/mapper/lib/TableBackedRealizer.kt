package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Realizer
import com.jayrave.falkon.mapper.Table

internal class TableBackedRealizer<T : Any>(private val table: Table<T, *>) : Realizer<T> {
    override fun realize(value: Realizer.Value): T {
        return table.create(object : Table.Value<T> {
            override fun <C> of(column: Column<T, C>): C {
                return value of column
            }
        })
    }
}