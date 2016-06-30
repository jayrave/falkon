package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.engine.CompiledInsert

interface InsertBuilder<T : Any> {

    val table: Table<T, *, *>

    /**
     * Calling this method again for a column that has already been set should overwrite the
     * existing value
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>
}


interface AdderOrEnder<T : Any> {

    /**
     * @see [set]
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>

    /**
     * @return [CompiledInsert] for this [InsertBuilder]
     */
    fun compile(): CompiledInsert
}