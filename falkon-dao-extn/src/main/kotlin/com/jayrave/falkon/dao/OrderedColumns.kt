package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import java.util.*

/**
 * To expose a collection of [Column] as an immutable set of ordered columns. Mostly used
 * for batch INSERT & UPDATE
 *
 * @param collection the collection of incoming columns
 * @param predicate to perform filtering on the passed in collection. Only elements for which
 * the predicate returns true are added
 */
internal class OrderedColumns<T : Any> private constructor(
        collection: Collection<Column<T, *>>, predicate: (Column<T, *>) -> Boolean) :
        List<Column<T, *>> {

    private val columns: List<Column<T, *>>
    init {
        val tempOrderedColumns = LinkedList<Column<T, *>>()
        collection.forEach {
            if (predicate.invoke(it)) {
                tempOrderedColumns.add(it)
            }
        }

        columns = tempOrderedColumns
    }

    override val size: Int get() = columns.size
    override fun contains(element: Column<T, *>) = columns.contains(element)
    override fun containsAll(elements: Collection<Column<T, *>>) = columns.containsAll(elements)
    override fun get(index: Int): Column<T, *> = columns[index]
    override fun indexOf(element: Column<T, *>) = columns.indexOf(element)
    override fun isEmpty() = columns.isEmpty()
    override fun iterator() = columns.iterator()
    override fun lastIndexOf(element: Column<T, *>) = columns.lastIndexOf(element)
    override fun listIterator() = columns.listIterator()
    override fun listIterator(index: Int) = columns.listIterator()
    override fun subList(fromIndex: Int, toIndex: Int) = columns.subList(fromIndex, toIndex)


    companion object {

        fun <T: Any> forAllColumnsOf(table: Table<T, *>): OrderedColumns<T> {
            return withPredicate(table) { true }
        }

        fun <T: Any> forNonIdColumnsOf(table: Table<T, *>): OrderedColumns<T> {
            return withPredicate(table) { it != table.idColumn }
        }

        private fun <T: Any> withPredicate(
                table: Table<T, *>, predicate: (Column<T, *>) -> Boolean): OrderedColumns<T> {
            // According to Table contract, allColumns must be thread-safe. So feel free
            // to just iterate over it without any special construct
            return OrderedColumns(table.allColumns, predicate)
        }
    }
}