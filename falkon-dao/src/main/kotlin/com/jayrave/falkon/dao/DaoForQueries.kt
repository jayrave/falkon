package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.Value
import com.jayrave.falkon.engine.Source
import java.util.*

/**
 * [T] that has the passed in [ID] as its primary key
 */
fun <T: Any, ID : Any> Dao<T, ID>.findById(id: ID): T? {
    val compiledQuery = queryBuilder()
            .where()
            .eq(table.idColumn, id)
            .limit(1) // to be defensive
            .compile()

    compiledQuery.safeCloseAfterOp {
        val source = execute()

        source.safeCloseAfterOp {
            val itemPresent = source.moveToFirst()
            return when (itemPresent) {
                true -> this@findById.createInstanceFrom(source)
                else -> null
            }
        }
    }
}


/**
 * All [T]s of this table
 */
fun <T: Any, ID : Any> Dao<T, ID>.findAll(): List<T> {
    val compiledQuery = queryBuilder().compile()
    compiledQuery.safeCloseAfterOp {
        val source = execute()

        source.safeCloseAfterOp {
            val dataProducer = SourceBackedDataProducer(source)
            val result = LinkedList<T>()
            while (source.moveToNext()) {
                result.add(createInstanceFrom(source, dataProducer))
            }

            return result
        }
    }
}


private fun <T: Any> Dao<T, *>.createInstanceFrom(
        source: Source,
        dataProducer: SourceBackedDataProducer = SourceBackedDataProducer(source)): T {

    return table.create(object : Value<T> {
        override fun <C> of(column: Column<T, C>): C {
            // Update data producer to point to the current column
            dataProducer.setColumnIndex(source.getColumnIndex(column.name))
            return column.computePropertyFrom(dataProducer)
        }
    })
}