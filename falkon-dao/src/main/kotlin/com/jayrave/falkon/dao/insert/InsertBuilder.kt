package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.engine.CompiledInsert
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table

interface InsertBuilder<T : Any> {

    val table: Table<T, *>

    /**
     * Sets the value a column should have after insert. This method can be called multiple times
     * to set values for multiple columns. Behaviour on calling this method again for a column
     * that has already been set is implementation dependent
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>
}


interface AdderOrEnder<T : Any> {

    /**
     * @see [set]
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>

    /**
     * @return [Insert] for this [InsertBuilder]
     */
    fun build(): Insert

    /**
     * @return [CompiledInsert] for this [InsertBuilder]
     */
    fun compile(): CompiledInsert
}