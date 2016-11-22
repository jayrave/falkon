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

internal class InsertOrReplaceBuilderImpl<T : Any>(
        override val table: Table<T, *>,
        private val insertOrReplaceSqlBuilder: InsertOrReplaceSqlBuilder) :
        InsertOrReplaceBuilder<T> {

    private val dataConsumerForIdColumns = LinkedHashMapBackedDataConsumer()
    private val dataConsumerForNonIdColumns = LinkedHashMapBackedDataConsumer()

    /**
     * Calling this method again for columns that have been already set will overwrite the
     * existing values for those columns
     */
    override fun values(setter: InnerSetter<T>.() -> Any?): Ender {
        InnerSetterImpl().setter()
        return EnderImpl()
    }


    private inner class EnderImpl : Ender {

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

        override fun compile(): CompiledStatement<Int> {
            val insertOrReplace = build()
            return table.configuration.engine
                    .compileInsertOrReplace(table.name, insertOrReplace.sql)
                    .closeIfOpThrows { bindAll(insertOrReplace.arguments) }
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
            val dataConsumer = when {
                column.isId -> dataConsumerForIdColumns
                else -> dataConsumerForNonIdColumns
            }

            dataConsumer.setColumnName(column.name)
            column.putStorageFormIn(value, dataConsumer)
        }
    }
}