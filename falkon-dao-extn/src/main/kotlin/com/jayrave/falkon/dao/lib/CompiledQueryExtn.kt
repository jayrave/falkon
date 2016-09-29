package com.jayrave.falkon.dao.lib

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.mapper.Value
import java.util.*

/**
 * Same as [extractFirstModel] but also closes [CompiledStatement]
 * @see extractFirstModel
 */
fun <T : Any> CompiledStatement<Source>.extractFirstModelAndClose(
        forTable: Table<T, *>, columnNameExtractor: ((Column<T, *>) -> String)): T? {

    return privateExtractAndClose {
        extractFirstModel(forTable, columnNameExtractor)
    }
}


/**
 * Same as [extractAllModels] but also closes [CompiledStatement]
 * @see extractAllModels
 */
fun <T : Any> CompiledStatement<Source>.extractAllModelsAndClose(
        forTable: Table<T, *>, toList: MutableList<T> = buildNewMutableList(),
        columnNameExtractor: ((Column<T, *>) -> String)): List<T> {

    return privateExtractAndClose {
        extractAllModels(forTable, toList, columnNameExtractor)
    }
}


/**
 * Same as [extractModels] but also closes [CompiledStatement]
 * @see extractModels
 */
fun <T : Any> CompiledStatement<Source>.extractModelsAndClose(
        forTable: Table<T, *>, toList: MutableList<T> = buildNewMutableList(),
        maxNumberOfModelsToExtract: Int = Int.MAX_VALUE,
        columnNameExtractor: ((Column<T, *>) -> String)): List<T> {

    return privateExtractAndClose {
        extractModels(forTable, toList, maxNumberOfModelsToExtract, columnNameExtractor)
    }
}


/**
 * Same as [extractModels] except that only the first model is extracted
 *
 * *NOTE:* [CompiledStatement] isn't closed after execution. If that is preferred, checkout
 * [extractFirstModelAndClose]
 *
 * @see extractFirstModelAndClose
 */
fun <T : Any> CompiledStatement<Source>.extractFirstModel(
        forTable: Table<T, *>, columnNameExtractor: ((Column<T, *>) -> String)): T? {

    val models = extractModels(
            forTable = forTable, toList = LinkedList(),
            maxNumberOfModelsToExtract = 1,
            columnNameExtractor = columnNameExtractor
    )

    return models.firstOrNull()
}


/**
 * Same as [extractModels] except that all models are extracted
 *
 * *NOTE:* [CompiledStatement] isn't closed after execution. If that is preferred, checkout
 * [extractAllModelsAndClose]
 *
 * @see extractAllModelsAndClose
 */
fun <T : Any> CompiledStatement<Source>.extractAllModels(
        forTable: Table<T, *>, toList: MutableList<T> = buildNewMutableList(),
        columnNameExtractor: ((Column<T, *>) -> String)): List<T> {

    return extractModels(
            forTable = forTable, toList = toList,
            columnNameExtractor = columnNameExtractor
    )
}


/**
 * Execute the [CompiledStatement] & extract an instance of [T] out of each row that is
 * returned. This is done through [Table.create]
 *
 * *NOTE:* [CompiledStatement] isn't closed after execution. If that is preferred, checkout
 * [extractModelsAndClose]
 *
 * @param [forTable] which manages the [T] & whose [Table.create] function is called
 * @param [toList] to which the created instances must be added. By default an [ArrayList] is used
 * @param [maxNumberOfModelsToExtract] at the most only these number of models will be extracted
 * @param [columnNameExtractor] used to extract the name that should be used to find the
 * appropriate column in [Source] that will created by executing this [CompiledStatement]
 *
 * @return is the same as [toList]
 * @see extractModelsAndClose
 */
fun <T : Any> CompiledStatement<Source>.extractModels(
        forTable: Table<T, *>, toList: MutableList<T> = buildNewMutableList(),
        maxNumberOfModelsToExtract: Int = Int.MAX_VALUE,
        columnNameExtractor: ((Column<T, *>) -> String)): List<T> {

    val source = execute()
    source.safeCloseAfterOp {
        val dataProducer = SourceBackedDataProducer(source)
        val columnIndexExtractor = buildColumnIndexExtractor(source, columnNameExtractor)
        while (source.moveToNext() && toList.size < maxNumberOfModelsToExtract) {
            toList.add(forTable.createInstanceFrom(dataProducer, columnIndexExtractor))
        }

        return toList
    }
}


/**
 * Performs the given operation & closes [CompiledStatement]
 */
private inline fun <R> CompiledStatement<Source>.privateExtractAndClose(operation: () -> R): R {
    return safeCloseAfterOp {
        operation.invoke()
    }
}


/**
 * By default [ArrayList] is used
 */
private fun <T> buildNewMutableList() = ArrayList<T>()


/**
 * Builds a column index extractor that looks up the index once & then caches it
 * for later use
 */
private fun <T : Any> buildColumnIndexExtractor(
        source: Source, columnNameExtractor: (Column<T, *>) -> String):
        (Column<T, *>) -> Int {

    val columnPositionMap = HashMap<Column<T, *>, Int?>()
    return { column: Column<T, *> ->
        var index = columnPositionMap[column] // Check if the index is in cache
        if (index == null) {
            val columnName = columnNameExtractor.invoke(column)
            columnPositionMap[column] = source.getColumnIndex(columnName) // Put in cache
            index = columnPositionMap[column]
        }

        index!!
    }
}


/**
 * Used to build a instance of [T]
 */
private fun <T : Any> Table<T, *>.createInstanceFrom(
        dataProducer: SourceBackedDataProducer,
        columnIndexExtractor: ((Column<T, *>) -> Int)): T {

    return create(object : Value<T> {
        override fun <C> of(column: Column<T, C>): C {
            // Update data producer to point to the current column
            dataProducer.setColumnIndex(columnIndexExtractor.invoke(column))
            return column.computePropertyFrom(dataProducer)
        }
    })
}