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

    bindColumns(columns, item, startIndex) { item, column ->
        @Suppress("UNCHECKED_CAST")
        (column as Column<Any, Any>).extractPropertyFrom(item)
    }

    return this
}


/**
 * values for [columns] extracted from [item] using [valueExtractor] will be bound using [bind].
 * If the extracted value is null, appropriate [TypedNull] is used
 */
internal inline fun <T: Any, ITEM, CS: CompiledStatement<R>, R> CS.bindColumns(
        columns: Collection<Column<T, *>>, item: ITEM, startIndex: Int = 1,
        valueExtractor: (ITEM, Column<T, *>) -> Any?): CS {

    columns.forEachIndexed { index, column ->
        bind(index + startIndex, valueExtractor.invoke(item, column) ?: TypedNull(column.dbType))
    }

    return this
}