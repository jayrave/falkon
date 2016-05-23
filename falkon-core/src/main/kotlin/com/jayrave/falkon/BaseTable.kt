package com.jayrave.falkon

import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Sink
import java.util.*
import kotlin.reflect.KProperty1

/**
 * An abstract extension of [Table] that could be sub-classed for easy & pain-free implementation of [Table]
 */
abstract class BaseTable<T : Any, ID : Any, E : Engine<S>, S : Sink>(
        override val name: String, override val configuration: TableConfiguration<E, S>) :
        Table<T, ID, E, S> {

    private final val _allColumns: MutableSet<Column<T, *>> = HashSet()
    override final val allColumns: Set<Column<T, *>>
        get() = _allColumns


    fun <C> col(
            property: KProperty1<T, C>,
            name: String = name(property),
            converter: Converter<C> = converter(property),
            nullFromSqlSubstitute: NullSubstitute<C> = nullFromSqlSubstitute(property),
            nullToSqlSubstitute: NullSubstitute<C> = nullToSqlSubstitute(property),
            propertyExtractor: PropertyExtractor<T, C> = SimplePropertyExtractor(property)): Column<T, C> {

        val column = ColumnImpl(name, propertyExtractor, converter, nullFromSqlSubstitute, nullToSqlSubstitute)
        _allColumns.add(column)
        return column
    }


    private fun <R> name(property: KProperty1<T, R>): String {
        return configuration.nameFormatter.format(property.name)
    }


    private fun <R> converter(property: KProperty1<T, R>): Converter<R> {
        return configuration.getConverter(property.returnType)
    }



    private class SimplePropertyExtractor<T : Any, C>(private val property: KProperty1<T, C>) :
            PropertyExtractor<T, C> {

        override fun extract(t: T) = property.get(t)
    }



    companion object {

        val nullReturningNullSubstitute = object : NullSubstitute<Any?> {
            override fun value() = null
        }

        val throwingNullFromSqlSubstitute = object : NullSubstitute<Any> {
            override fun value() = throw NullPointerException(
                    "SQL value for a non-null type property is null!!. Solution: make db column non-null or " +
                            "provide a valid ${NullSubstitute::class}"
            )
        }

        val throwingNullToSqlSubstitute = object : NullSubstitute<Any> {
            override fun value() = throw NullPointerException("Something is wrong!! Let the developer know")
        }


        private fun <T, C> nullFromSqlSubstitute(property: KProperty1<T, C>): NullSubstitute<C> {
            @Suppress("UNCHECKED_CAST")
            return when (property.returnType.isMarkedNullable) {
                true -> nullReturningNullSubstitute
                false -> throwingNullFromSqlSubstitute
            } as NullSubstitute<C>
        }


        private fun <T, C> nullToSqlSubstitute(property: KProperty1<T, C>): NullSubstitute<C> {
            @Suppress("UNCHECKED_CAST")
            return when (property.returnType.isMarkedNullable) {
                true -> nullReturningNullSubstitute
                false -> throwingNullToSqlSubstitute
            } as NullSubstitute<C>
        }
    }
}