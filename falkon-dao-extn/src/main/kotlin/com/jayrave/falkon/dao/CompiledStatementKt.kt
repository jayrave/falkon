package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.engine.bind

/**
 * all columns in [orderedColumns] of [item] will be bound using [bind]. If the column value
 * is null, appropriate [TypedNull] is used
 */
internal fun <T: Any, CS: CompiledStatement<R>, R> CS.bindOrderedColumns(
        orderedColumns: OrderedColumns<T>, item: T, startIndex: Int = 1): CS {

    orderedColumns.forEachIndexed { index, column ->
        bind(index + startIndex, column.extractPropertyFrom(item) ?: TypedNull(column.dbType))
    }

    return this
}