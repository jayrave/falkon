package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink

interface InsertBuilder<T : Any, E: Engine<S>, S : Sink> {

    val table: Table<T, *, E, S>

    /**
     * Calling this method again for a column that has already been set should overwrite the existing value
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>
}


interface AdderOrEnder<T : Any> {

    /**
     * @see [InsertBuilder.set]
     */
    fun <C> set(column: Column<T, C>, value: C): AdderOrEnder<T>

    /**
     * @return - the row ID of the newly inserted row
     */
    fun insert(): Long
}