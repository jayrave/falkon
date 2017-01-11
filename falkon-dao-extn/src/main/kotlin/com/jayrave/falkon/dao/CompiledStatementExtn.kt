package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.engine.bind
import com.jayrave.falkon.mapper.Column

/**
 * Values for [columns] of [item] will be bound using [bind]. If the column value is null,
 * appropriate [TypedNull] is used
 */
internal fun <T: Any, CS: CompiledStatement<R>, R> CS.bindColumns(
        columns: Collection<Column<T, *>>, item: T, startIndex: Int = 1): CS {

    bindColumns(columns, item, startIndex) { item, column ->
        column.extractPropertyFrom(item)
    }

    return this
}


/**
 * Values for [columns] extracted from [item] using [valueExtractor] will be bound using [bind]
 * after converting them into their storage form. If the extracted value is null, appropriate
 * [TypedNull] is used
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <T: Any, ITEM, CS: CompiledStatement<R>, R> CS.bindColumns(
        columns: Collection<Column<T, *>>, item: ITEM, startIndex: Int = 1,
        valueExtractor: (ITEM, Column<T, *>) -> Any?): CS {

    columns.forEachIndexed { index, column ->
        val columnValue = valueExtractor.invoke(item, column)
        val storageForm = (column as Column<T, Any?>).computeStorageFormOf(columnValue)
        bind(index + startIndex, storageForm ?: TypedNull(column.dbType))
    }

    return this
}