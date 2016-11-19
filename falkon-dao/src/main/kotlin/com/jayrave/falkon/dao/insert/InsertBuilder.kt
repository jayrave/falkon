package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table

interface InsertBuilder<T : Any> {

    val table: Table<T, *>

    /**
     * Sets the values to be inserted for columns. Behaviour on calling this method multiple
     * times is implementation dependent
     */
    fun values(setter: InnerSetter<T>.() -> Any?): Ender
}


interface Ender {

    /**
     * @return [Insert] for this [InsertBuilder]
     */
    fun build(): Insert

    /**
     * @return [CompiledStatement] for this [InsertBuilder]
     */
    fun compile(): CompiledStatement<Int>
}


interface InnerSetter<T : Any> {

    /**
     * Sets the value a column should have after insert. This method can be called multiple times
     * to set values for multiple columns. Behaviour on calling this method again for a column
     * that has already been set is implementation dependent
     */
    fun <C> set(column: Column<T, C>, value: C)
}