package com.jayrave.falkon.dao.insertOrReplace

import com.jayrave.falkon.dao.lib.LinkedHashMapBackedDataConsumer
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.engine.safeCloseAfterExecution
import com.jayrave.falkon.iterables.IterableBackedIterable
import com.jayrave.falkon.iterables.IterablesBackedIterable
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.InsertOrReplaceSqlBuilder
import java.sql.SQLException
import java.util.*

internal class InsertOrReplaceBuilderImpl<T : Any>(
        override val table: Table<T, *>,
        private val insertOrReplaceSqlBuilder: InsertOrReplaceSqlBuilder) :
        InsertOrReplaceBuilder<T> {

    private val dataConsumerForIdColumns = LinkedHashMapBackedDataConsumer()
    private val dataConsumerForNonIdColumns = LinkedHashMapBackedDataConsumer()

    // This index map is built under the following assumptions
    //  - all id columns are bound first followed by the non id columns (in the order of arrival)
    //  - both the data consumers (id & non-id) are backed by maps that maintain insertion order
    // which doesn't change on remapping
    private val indexMap = LinkedList<Int>()
    init {
        // Index 0 is invalid for index map as the mapping starts at 1
        indexMap.add(Int.MIN_VALUE)
    }


    /**
     * Calling this method again for columns that have been already set will overwrite the
     * existing values for those columns
     */
    override fun values(setter: InnerSetter<T>.() -> Any?): Ender {
        InnerSetterImpl().setter()
        return EnderImpl()
    }



    private inner class EnderImpl : Ender {

        /**
         * @return [InsertOrReplace]'s SQL statement could have the columns & arguments
         * in a different order than they were injected via [InnerSetter.set]
         */
        override fun build(): InsertOrReplace {
            val mapForIdColumns = dataConsumerForIdColumns.map
            val mapForNonIdColumns = dataConsumerForNonIdColumns.map
            val sql = insertOrReplaceSqlBuilder.build(
                    table.name, IterableBackedIterable.create(mapForIdColumns.keys),
                    IterableBackedIterable.create(mapForNonIdColumns.keys)
            )

            val arguments = IterablesBackedIterable(listOf(
                    IterableBackedIterable.create(mapForIdColumns.values),
                    IterableBackedIterable.create(mapForNonIdColumns.values)
            ))

            return InsertOrReplaceImpl(table.name, sql, arguments)
        }


        /**
         * @return [CompiledStatement] which can be reused to perform multiple insert or replaces
         * by binding different values for columns in the same order as they were injected
         * via [InnerSetter.set]
         */
        override fun compile(): CompiledStatement<Int> {
            // Build insert or replace
            val insertOrReplace = build()

            // Compile insert or replace & bind all arguments
            val compiledInsertOrReplace = table
                    .configuration
                    .engine
                    .compileInsertOrReplace(table.name, insertOrReplace.sql)
                    .closeIfOpThrows { bindAll(insertOrReplace.arguments) }

            // Remap indices as id columns & non id columns may have come out of order
            val indexRemappingCompiledStatement = IndexRemappingCompiledStatement(
                    compiledInsertOrReplace, indexMap.toIntArray()
            )

            // Further bindings can happen in the order the columns arrived in & the
            // IndexRemappingCompiledStatement will take care of everything
            return indexRemappingCompiledStatement
        }


        override fun insertOrReplace() {
            val numberOfRecordsInsertedOrReplaced = compile().safeCloseAfterExecution()
            if (numberOfRecordsInsertedOrReplaced != 1) {
                throw SQLException(
                        "Number of records inserted or replaced: " +
                                "$numberOfRecordsInsertedOrReplaced. It should have been 1"
                )
            }
        }
    }



    private inner class InnerSetterImpl : InnerSetter<T> {

        /**
         * Calling this method again for a column that has been already set will overwrite the
         * existing value for that column
         */
        override fun <C> set(column: Column<T, C>, value: C) {
            val isIdColumn = column.isId
            val dataConsumer = when {
                isIdColumn -> dataConsumerForIdColumns
                else -> dataConsumerForNonIdColumns
            }

            // If mapping existed, it will be rebound, which doesn't change the target index
            val columnName = column.name
            val addToIndexMap = !dataConsumer.map.containsKey(columnName)

            // Consume value
            dataConsumer.setColumnName(columnName)
            column.putStorageFormIn(value, dataConsumer)

            // Add to index map if required
            if (addToIndexMap) {

                val fromIndex = indexMap.size // Where the client would bind
                val toIndex = when { // Where the actual argument should be bound
                    isIdColumn -> dataConsumerForIdColumns.map.size // Data just got inserted here
                    else -> indexMap.size
                }

                // Create mapping
                indexMap.add(fromIndex, toIndex)

                // If fromIndex isn't the same as toIndex, someone just cut into the line.
                // Adjust other mappings based on last computed toIndex.
                if (fromIndex != toIndex) {
                    indexMap.forEachIndexed { index, targetIndex ->

                        // Don't update last mapping. For other indices if it is >= toIndex,
                        // it must be incremented by 1
                        if (index != fromIndex && targetIndex >= toIndex) {
                            indexMap[index] = targetIndex + 1
                        }
                    }
                }
            }
        }
    }
}