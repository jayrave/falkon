package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.ReadOnlyColumn
import com.jayrave.falkon.mapper.Realizer
import com.jayrave.falkon.mapper.Table
import java.util.*

/**
 * Same as [extractFirstModel] but also closes [CompiledStatement]
 * @see extractFirstModel
 */
fun <T : Any> CompiledStatement<Source>.extractFirstModelAndClose(
        forTable: Table<T, *>, columnNameExtractor: ((Column<T, *>) -> String)): T? {

    return extractFirstModelAndClose(
            forTable.buildRealizer(),
            columnNameExtractor.castToUseWithRealizer()
    )
}


/**
 * Same as [extractAllModels] but also closes [CompiledStatement]
 * @see extractAllModels
 */
fun <T : Any> CompiledStatement<Source>.extractAllModelsAndClose(
        forTable: Table<T, *>, toList: MutableList<T> = buildNewMutableList(),
        columnNameExtractor: ((Column<T, *>) -> String)): List<T> {

    return extractAllModelsAndClose(
            forTable.buildRealizer(), toList,
            columnNameExtractor.castToUseWithRealizer()
    )
}


/**
 * Same as [extractModels] but also closes [CompiledStatement]
 * @see extractModels
 */
fun <T : Any> CompiledStatement<Source>.extractModelsAndClose(
        forTable: Table<T, *>, toList: MutableList<T> = buildNewMutableList(),
        maxNumberOfModelsToExtract: Int = Int.MAX_VALUE,
        columnNameExtractor: ((Column<T, *>) -> String)): List<T> {

    return extractModelsAndClose(
            forTable.buildRealizer(), toList, maxNumberOfModelsToExtract,
            columnNameExtractor.castToUseWithRealizer()
    )
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

    return extractFirstModel(
            forTable.buildRealizer(),
            columnNameExtractor.castToUseWithRealizer()
    )
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

    return extractAllModels(
            forTable.buildRealizer(), toList,
            columnNameExtractor.castToUseWithRealizer()
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

    return extractModels(
            forTable.buildRealizer(), toList, maxNumberOfModelsToExtract,
            columnNameExtractor.castToUseWithRealizer()
    )
}


/**
 * Same as [extractFirstModel] but also closes [CompiledStatement]
 * @see extractFirstModel
 */
fun <T : Any> CompiledStatement<Source>.extractFirstModelAndClose(
        realizer: Realizer<T>, columnNameExtractor: ((ReadOnlyColumn<*>) -> String)): T? {

    return safeCloseAfterOp {
        extractFirstModel(realizer, columnNameExtractor)
    }
}


/**
 * Same as [extractAllModels] but also closes [CompiledStatement]
 * @see extractAllModels
 */
fun <T : Any> CompiledStatement<Source>.extractAllModelsAndClose(
        realizer: Realizer<T>, toList: MutableList<T> = buildNewMutableList(),
        columnNameExtractor: ((ReadOnlyColumn<*>) -> String)): List<T> {

    return safeCloseAfterOp {
        extractAllModels(realizer, toList, columnNameExtractor)
    }
}


/**
 * Same as [extractModels] but also closes [CompiledStatement]
 * @see extractModels
 */
fun <T : Any> CompiledStatement<Source>.extractModelsAndClose(
        realizer: Realizer<T>, toList: MutableList<T> = buildNewMutableList(),
        maxNumberOfModelsToExtract: Int = Int.MAX_VALUE,
        columnNameExtractor: ((ReadOnlyColumn<*>) -> String)): List<T> {

    return safeCloseAfterOp {
        extractModels(realizer, toList, maxNumberOfModelsToExtract, columnNameExtractor)
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
        realizer: Realizer<T>, columnNameExtractor: ((ReadOnlyColumn<*>) -> String)): T? {

    val models = extractModels(
            realizer = realizer, toList = LinkedList(),
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
        realizer: Realizer<T>, toList: MutableList<T> = buildNewMutableList(),
        columnNameExtractor: ((ReadOnlyColumn<*>) -> String)): List<T> {

    return extractModels(
            realizer = realizer, toList = toList,
            columnNameExtractor = columnNameExtractor
    )
}


/**
 * Execute the [CompiledStatement] & extract an instance of [T] out of each row that is
 * returned. This is done through [Realizer.realize]
 *
 * *NOTE:* [CompiledStatement] isn't closed after execution. If that is preferred, checkout
 * [extractModelsAndClose]
 *
 * @param [realizer] whose [Realizer.realize] function will be called to realize instance
 * @param [toList] to which the realized instances must be added. By default an [ArrayList] is used
 * @param [maxNumberOfModelsToExtract] at the most only these number of models will be extracted
 * @param [columnNameExtractor] used to extract the name that should be used to find the
 * appropriate column in [Source] that will created by executing this [CompiledStatement]
 *
 * @return is the same as [toList]
 * @see extractModelsAndClose
 */
fun <T : Any> CompiledStatement<Source>.extractModels(
        realizer: Realizer<T>, toList: MutableList<T> = buildNewMutableList(),
        maxNumberOfModelsToExtract: Int = Int.MAX_VALUE,
        columnNameExtractor: ((ReadOnlyColumn<*>) -> String)): List<T> {

    val source = execute()
    source.safeCloseAfterOp {
        val dataProducer = SourceBackedDataProducer(source)
        val columnIndexExtractor = buildColumnIndexExtractor(source, columnNameExtractor)
        while (source.moveToNext() && toList.size < maxNumberOfModelsToExtract) {
            toList.add(realizer.createInstanceFrom(dataProducer, columnIndexExtractor))
        }

        return toList
    }
}


/**
 * By default [ArrayList] is used
 */
private fun <T> buildNewMutableList() = ArrayList<T>()


/**
 * Builds a [Realizer] backed by [Table]
 */
private fun <T : Any> Table<T, *>.buildRealizer(): Realizer<T> = TableBackedRealizer(this)


@Suppress("UNCHECKED_CAST")
private fun <T : Any> ((Column<T, *>) -> String).castToUseWithRealizer():
        ((ReadOnlyColumn<*>) -> String) = this as ((ReadOnlyColumn<*>) -> String)


/**
 * Builds a column index extractor that looks up the index once & then caches it
 * for later use
 */
private fun buildColumnIndexExtractor(
        source: Source, columnNameExtractor: (ReadOnlyColumn<*>) -> String):
        (ReadOnlyColumn<*>) -> Int {

    val columnPositionMap = HashMap<ReadOnlyColumn<*>, Int>()
    return { column: ReadOnlyColumn<*> ->
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
private fun <T : Any> Realizer<T>.createInstanceFrom(
        dataProducer: SourceBackedDataProducer,
        columnIndexExtractor: ((ReadOnlyColumn<*>) -> Int)): T {

    return realize(object : Realizer.Value {
        override fun <C> of(column: ReadOnlyColumn<C>): C {
            // Update data producer to point to the current column
            dataProducer.setColumnIndex(columnIndexExtractor.invoke(column))
            return column.computePropertyFrom(dataProducer)
        }
    })
}