package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.engine.bind
import com.jayrave.falkon.mapper.Column

/**
 * [columns] of [item] will be bound using [bind]. If the column value is null,
 * appropriate [TypedNull] is used
 */
internal fun <T: Any, CS: CompiledStatement<R>, R> CS.bindColumns(
        columns: Collection<Column<T, *>>, item: T, startIndex: Int = 1): CS {

    columns.forEachIndexed { index, column ->
        bind(index + startIndex, column.extractPropertyFrom(item) ?: TypedNull(column.dbType))
    }

    return this
}