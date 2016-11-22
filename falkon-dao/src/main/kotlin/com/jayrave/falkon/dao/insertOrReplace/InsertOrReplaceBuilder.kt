package com.jayrave.falkon.dao.insertOrReplace

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table

interface InsertOrReplaceBuilder<T : Any> {

    val table: Table<T, *>

    /**
     * Sets the values to be inserted or replaced with for columns. Behaviour on calling
     * this method multiple times is implementation dependent
     */
    fun values(setter: InnerSetter<T>.() -> Any?): Ender
}


interface Ender {

    /**
     * *Note:* Look at implementation for how this works. The behavior may differ from one
     * database to another
     *
     * @return [InsertOrReplace] for this builder
     */
    fun build(): InsertOrReplace

    /**
     * *Note:* Look at implementation for how this works. The behavior may differ from one
     * database to another
     *
     * @return [CompiledStatement] for this builder
     */
    fun compile(): CompiledStatement<Int>

    /**
     * *Note:* Look at implementation for how this works. The behavior may differ from one
     * database to another
     *
     * Inserts or replaces record represented by this builder
     */
    fun insertOrReplace()
}


interface InnerSetter<T : Any> {

    /**
     * Sets the value a column should have after insert or replace. This method can be called
     * multiple times to set values for multiple columns. Behaviour on calling this method
     * again for a column that has already been set is implementation dependent
     */
    fun <C> set(column: Column<T, C>, value: C)
}