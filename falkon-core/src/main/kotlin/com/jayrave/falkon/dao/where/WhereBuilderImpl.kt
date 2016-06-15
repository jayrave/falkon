package com.jayrave.falkon.dao.where

import com.jayrave.falkon.Column
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.engine.WhereSection
import com.jayrave.falkon.engine.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.engine.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.engine.WhereSection.Predicate.*
import com.jayrave.falkon.exceptions.SQLSyntaxErrorException
import java.util.*

/**
 * [Z] instantiated by the passed in creator function must call through (for [AdderOrEnder]
 * methods) to the instance of [WhereBuilderImpl] provided while invoking the creator
 */
internal class WhereBuilderImpl<T : Any, Z : AdderOrEnder<T, Z>>(
        adderOrEnderCreator: (WhereBuilderImpl<T, Z>) -> Z) :
        WhereBuilder<T, Z>,
        AdderOrEnder<T, Z>,
        AfterSimpleConnectorAdder<T, Z> {

    private val sections: MutableList<ArgAwareWhereSection> = LinkedList()
    private val adderOrEnder: Z by lazy { adderOrEnderCreator.invoke(this) }

    internal fun build(): Where {
        return Where(ListBackedList(sections, transformer), ArgsIterable(sections))
    }


    override fun <C> eq(column: Column<T, C>, value: C): Z {
        handleEq(sections, column, value)
        return adderOrEnder
    }


    override fun <C> notEq(column: Column<T, C>, value: C): Z {
        handleNotEq(sections, column, value)
        return adderOrEnder
    }


    override fun <C> gt(column: Column<T, C>, value: C): Z {
        handleGt(sections, column, value)
        return adderOrEnder
    }


    override fun <C> ge(column: Column<T, C>, value: C): Z {
        handleGe(sections, column, value)
        return adderOrEnder
    }


    override fun <C> lt(column: Column<T, C>, value: C): Z {
        handleLt(sections, column, value)
        return adderOrEnder
    }


    override fun <C> le(column: Column<T, C>, value: C): Z {
        handleLe(sections, column, value)
        return adderOrEnder
    }


    override fun <C> between(column: Column<T, C>, low: C, high: C): Z {
        handleBetween(sections, column, low, high)
        return adderOrEnder
    }


    override fun <C> like(column: Column<T, C>, pattern: String): Z {
        handleLike(sections, column, pattern)
        return adderOrEnder
    }


    override fun <C> isNull(column: Column<T, C>): Z {
        handleIsNull(sections, column)
        return adderOrEnder
    }


    override fun <C> isNotNull(column: Column<T, C>): Z {
        handleIsNotNull(sections, column)
        return adderOrEnder
    }


    override fun and(): AfterSimpleConnectorAdder<T, Z> {
        sections.add(ArgAwareWhereSection(SimpleConnector(SimpleConnector.Type.AND), emptyList()))
        return this
    }


    override fun or(): AfterSimpleConnectorAdder<T, Z> {
        sections.add(ArgAwareWhereSection(SimpleConnector(SimpleConnector.Type.OR), emptyList()))
        return this
    }


    override fun and(predicate: InnerAdder<T>.() -> Any?): Z {
        handleAnd(sections, predicate)
        return adderOrEnder
    }


    override fun or(predicate: InnerAdder<T>.() -> Any?): Z {
        handleOr(sections, predicate)
        return adderOrEnder
    }



    private class InnerAdderImpl<T : Any> : InnerAdder<T> {

        val sections: MutableList<ArgAwareWhereSection> = LinkedList()

        override fun <C> eq(column: Column<T, C>, value: C) = handleEq(sections, column, value)
        override fun <C> notEq(column: Column<T, C>, value: C) = handleNotEq(
                sections, column, value
        )

        override fun <C> gt(column: Column<T, C>, value: C) = handleGt(sections, column, value)
        override fun <C> ge(column: Column<T, C>, value: C) = handleGe(sections, column, value)
        override fun <C> lt(column: Column<T, C>, value: C) = handleLt(sections, column, value)
        override fun <C> le(column: Column<T, C>, value: C) = handleLe(sections, column, value)
        override fun <C> between(column: Column<T, C>, low: C, high: C) = handleBetween(
                sections, column, low, high
        )

        override fun <C> like(column: Column<T, C>, pattern: String) = handleLike(
                sections, column, pattern
        )

        override fun <C> isNull(column: Column<T, C>) = handleIsNull(sections, column)
        override fun <C> isNotNull(column: Column<T, C>) = handleIsNotNull(sections, column)
        override fun and(predicate: InnerAdder<T>.() -> Any?) = handleAnd(sections, predicate)
        override fun or(predicate: InnerAdder<T>.() -> Any?) = handleOr(sections, predicate)
    }



    private data class ArgAwareWhereSection(
            val whereSection: WhereSection,
            val args: Iterable<Any>
    )



    private class ArgsIterable(private val sections: List<ArgAwareWhereSection>) : Iterable<Any> {

        override fun iterator(): Iterator<Any> = ArgAwareWhereSectionsBackedIterator()

        private inner class ArgAwareWhereSectionsBackedIterator : Iterator<Any> {

            private var iterator = emptyList<Any>().iterator()
            private var currentSectionIndex = -1

            override fun hasNext(): Boolean {
                var hasNext = iterator.hasNext()
                while (!hasNext && currentSectionIndex < sections.size - 1) {
                    // This args list has run out. Move on to the next one
                    iterator = sections[++currentSectionIndex].args.iterator()
                    hasNext = iterator.hasNext()
                }

                return hasNext
            }

            override fun next(): Any {
                return iterator.next()
            }
        }
    }



    companion object {
        
        private val transformer: (ArgAwareWhereSection) -> WhereSection = { it.whereSection }

        private fun <T: Any, C> handleEq(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, value: C) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.EQ, column.name),
                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleNotEq(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, value: C) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.NOT_EQ, column.name),
                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleGt(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, value: C) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, column.name),
                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleGe(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, value: C) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, column.name),
                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleLt(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, value: C) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.LESS_THAN, column.name),
                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleLe(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, value: C) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, column.name),
                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleBetween(
                sections: MutableList<ArgAwareWhereSection>,
                column: Column<T, C>, low: C, high: C) {

            sections.add(ArgAwareWhereSection(
                    BetweenPredicate(column.name),
                    listOf(getAppropriateArg(column, low), getAppropriateArg(column, high))
            ))
        }


        private fun <T: Any, C> handleLike(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, pattern: String) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(OneArgPredicate.Type.LIKE, column.name),
                    listOf(pattern)
            ))
        }


        private fun <T: Any, C> handleIsNull(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>) {

            sections.add(ArgAwareWhereSection(
                    NoArgPredicate(NoArgPredicate.Type.IS_NULL, column.name),
                    emptyList()
            ))
        }


        private fun <T: Any, C> handleIsNotNull(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>) {

            sections.add(ArgAwareWhereSection(
                    NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, column.name),
                    emptyList()
            ))
        }


        private fun <T: Any> handleAnd(
                sections: MutableList<ArgAwareWhereSection>, predicate: InnerAdder<T>.() -> Any?) {

            val innerAdder = InnerAdderImpl<T>()
            innerAdder.predicate()
            val innerAdderSections = innerAdder.sections
            throwIfSectionsIfEmpty(
                    innerAdderSections, "At least 1 predicate should be added inside and{}"
            )

            sections.add(ArgAwareWhereSection(
                    CompoundConnector(
                            CompoundConnector.Type.AND,
                            ListBackedList(innerAdderSections, transformer)
                    ), ArgsIterable(innerAdderSections)
            ))
        }


        private fun <T: Any> handleOr(
                sections: MutableList<ArgAwareWhereSection>, predicate: InnerAdder<T>.() -> Any?) {

            val innerAdder = InnerAdderImpl<T>()
            innerAdder.predicate()
            val innerAdderSections = innerAdder.sections
            throwIfSectionsIfEmpty(
                    innerAdderSections, "At least 1 predicate should be added inside or{}"
            )

            sections.add(ArgAwareWhereSection(
                    CompoundConnector(
                            CompoundConnector.Type.OR,
                            ListBackedList(innerAdderSections, transformer)
                    ), ArgsIterable(innerAdderSections)
            ))
        }


        private fun <T: Any, C> getAppropriateArg(column: Column<T, C>, value: C): Any {
            return when (value) {
                null -> TypedNull(column.dbType)
                else -> value as Any
            }
        }


        private fun throwIfSectionsIfEmpty(sections: List<ArgAwareWhereSection>, error: String) {
            if (sections.isEmpty()) {
                throw SQLSyntaxErrorException(error)
            }
        }
    }
}