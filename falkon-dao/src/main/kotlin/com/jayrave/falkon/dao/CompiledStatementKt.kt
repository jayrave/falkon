package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.engine.bind

/**
 * [column] of [item] will be bound using [bind]. If the column value is null, appropriate
 * [TypedNull] is used
 */
internal fun <T: Any, CS: CompiledStatement<R>, R> CS.bindColumn(
        index: Int, column: Column<T, *>, item: T): CS {

    bind(index + 1, column.extractPropertyFrom(item) ?: TypedNull(column.dbType))
    return this
}