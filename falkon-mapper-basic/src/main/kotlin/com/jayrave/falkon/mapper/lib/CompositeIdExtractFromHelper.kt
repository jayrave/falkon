package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import java.util.*
import kotlin.reflect.KProperty1

/**
 * Utility class to simplify implementation of [Table.extractFrom] for tables with
 * composite ids i.e., for tables where multiple columns together serve as the primary key
 */
class CompositeIdExtractFromHelper<in ID : Any> private constructor(
        private val columnToPropertyMap: Map<Column<*, *>, KProperty1<ID, *>>) {

    fun <C> extractFrom(id: ID, column: Column<*, C>): C {
        column.throwIfNotValidCandidateForExtractFrom()
        val matchingColumn = columnToPropertyMap.findColumnWithName(column.name)

        @Suppress("UNCHECKED_CAST")
        return when {
            matchingColumn != null -> columnToPropertyMap[matchingColumn]!!.get(id) as C
            else -> throwSinceExtractFromHelperDoesNotKnowAboutColumn(
                    CompositeIdExtractFromHelper::class.qualifiedName!!, column
            )
        }
    }



    class Builder<ID : Any> {

        private val map = HashMap<Column<*, *>, KProperty1<ID, *>>()

        fun <C> add(column: Column<*, C>, property: KProperty1<ID, C>): Builder<ID> {
            column.throwIfNotValidCandidateForExtractFrom()
            map.put(column, property)
            return this
        }

        fun build(): CompositeIdExtractFromHelper<ID> {
            return CompositeIdExtractFromHelper(HashMap(map))
        }
    }



    companion object {

        private fun Map<Column<*, *>, *>.findColumnWithName(name: String): Column<*, *>? {
            this.forEach {
                if (it.key.name == name) {
                    return it.key
                }
            }

            // No matching columns found!
            return null
        }
    }
}