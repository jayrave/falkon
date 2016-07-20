package com.jayrave.falkon.dao.where.lenient

import com.jayrave.falkon.dao.lib.IterablesBackedIterable
import com.jayrave.falkon.dao.lib.getAppropriateName
import com.jayrave.falkon.dao.where.Where
import com.jayrave.falkon.dao.where.WhereImpl
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.*
import java.sql.SQLSyntaxErrorException
import java.util.*

/**
 * [Z] instantiated by the passed in creator function must call through (for [AdderOrEnder]
 * methods) to the instance of [AdderOrEnder] provided while invoking the creator
 */
internal class WhereBuilderImpl<Z : AdderOrEnder<Z>>(
        private val qualifyColumnNames: Boolean,
        adderOrEnderCreator: (AdderOrEnder<Z>) -> Z) :
        WhereBuilder<Z>,
        AdderOrEnder<Z>,
        AfterSimpleConnectorAdder<Z> {

    private val sections: MutableList<ArgAwareWhereSection> = LinkedList()
    private val adderOrEnder: Z by lazy { adderOrEnderCreator.invoke(this) }


    internal fun build(): Where {
        return WhereImpl(
                ListBackedList(sections, transformerForList),
                IterablesBackedIterable(sections)
        )
    }


    override fun <T : Any, C> eq(column: Column<T, C>, value: C): Z {
        handleEq(sections, column, value, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> notEq(column: Column<T, C>, value: C): Z {
        handleNotEq(sections, column, value, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> gt(column: Column<T, C>, value: C): Z {
        handleGt(sections, column, value, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> ge(column: Column<T, C>, value: C): Z {
        handleGe(sections, column, value, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> lt(column: Column<T, C>, value: C): Z {
        handleLt(sections, column, value, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> le(column: Column<T, C>, value: C): Z {
        handleLe(sections, column, value, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> between(column: Column<T, C>, low: C, high: C): Z {
        handleBetween(sections, column, low, high, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> like(column: Column<T, C>, pattern: String): Z {
        handleLike(sections, column, pattern, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> isIn(
            column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z {

        handleIsIn(sections, column, firstValue, remainingValues, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> isNotIn(
            column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z {

        handleIsNotIn(sections, column, firstValue, remainingValues, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> isNull(column: Column<T, C>): Z {
        handleIsNull(sections, column, qualifyColumnNames)
        return adderOrEnder
    }


    override fun <T : Any, C> isNotNull(column: Column<T, C>): Z {
        handleIsNotNull(sections, column, qualifyColumnNames)
        return adderOrEnder
    }


    override fun and(predicate: InnerAdder.() -> Any?): Z {
        handleAnd(sections, predicate, qualifyColumnNames)
        return adderOrEnder
    }


    override fun or(predicate: InnerAdder.() -> Any?): Z {
        handleOr(sections, predicate, qualifyColumnNames)
        return adderOrEnder
    }


    override fun and(): AfterSimpleConnectorAdder<Z> {
        sections.add(ArgAwareWhereSection(
                SimpleConnector(SimpleConnector.Type.AND), emptyList()
        ))

        return this
    }


    override fun or(): AfterSimpleConnectorAdder<Z> {
        sections.add(ArgAwareWhereSection(
                SimpleConnector(SimpleConnector.Type.OR), emptyList()
        ))

        return this
    }



    private class InnerAdderImpl(private val qualifyColumnNames: Boolean) : InnerAdder {

        val sections: MutableList<ArgAwareWhereSection> = LinkedList()

        override fun <T : Any, C> eq(column: Column<T, C>, value: C) {
            handleEq(sections, column, value, qualifyColumnNames)
        }

        override fun <T : Any, C> notEq(column: Column<T, C>, value: C) {
            handleNotEq(sections, column, value, qualifyColumnNames)
        }

        override fun <T : Any, C> gt(column: Column<T, C>, value: C) {
            handleGt(sections, column, value, qualifyColumnNames)
        }

        override fun <T : Any, C> ge(column: Column<T, C>, value: C) {
            handleGe(sections, column, value, qualifyColumnNames)
        }

        override fun <T : Any, C> lt(column: Column<T, C>, value: C) {
            handleLt(sections, column, value, qualifyColumnNames)
        }

        override fun <T : Any, C> le(column: Column<T, C>, value: C) {
            handleLe(sections, column, value, qualifyColumnNames)
        }

        override fun <T : Any, C> between(column: Column<T, C>, low: C, high: C) {
            handleBetween(sections, column, low, high, qualifyColumnNames)
        }

        override fun <T : Any, C> like(column: Column<T, C>, pattern: String) {
            handleLike(sections, column, pattern, qualifyColumnNames)
        }

        override fun <T : Any, C> isIn(
                column: Column<T, C>, firstValue: C, vararg remainingValues: C) {

            handleIsIn(sections, column, firstValue, remainingValues, qualifyColumnNames)
        }

        override fun <T : Any, C> isNotIn(
                column: Column<T, C>, firstValue: C, vararg remainingValues: C) {

            handleIsNotIn(sections, column, firstValue, remainingValues, qualifyColumnNames)
        }

        override fun <T : Any, C> isNull(column: Column<T, C>) {
            handleIsNull(sections, column, qualifyColumnNames)
        }

        override fun <T : Any, C> isNotNull(column: Column<T, C>) {
            handleIsNotNull(sections, column, qualifyColumnNames)
        }

        override fun and(predicate: InnerAdder.() -> Any?) {
            handleAnd(sections, predicate, qualifyColumnNames)
        }

        override fun or(predicate: InnerAdder.() -> Any?) {
            handleOr(sections, predicate, qualifyColumnNames)
        }
    }



    private data class ArgAwareWhereSection(
            val whereSection: WhereSection, val args: Iterable<Any>) :
            Iterable<Any> {

        override fun iterator() = args.iterator()
    }



    companion object {
        
        private val transformerForList: (ArgAwareWhereSection) -> WhereSection = { it.whereSection }

        private fun <T: Any, C> handleEq(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                value: C, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.EQ,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleNotEq(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                value: C, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.NOT_EQ,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleGt(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                value: C, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.GREATER_THAN,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleGe(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                value: C, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.GREATER_THAN_OR_EQ,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleLt(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                value: C, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.LESS_THAN,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleLe(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                value: C, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.LESS_THAN_OR_EQ,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(getAppropriateArg(column, value))
            ))
        }


        private fun <T: Any, C> handleBetween(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                low: C, high: C, qualifyColumnNames: Boolean) {

            sections.add(ArgAwareWhereSection(
                    BetweenPredicate(column.getAppropriateName(qualifyColumnNames)),
                    listOf(getAppropriateArg(column, low), getAppropriateArg(column, high))
            ))
        }


        private fun <T: Any, C> handleLike(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                pattern: String, qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    OneArgPredicate(
                            OneArgPredicate.Type.LIKE,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    listOf(pattern)
            ))
        }


        private fun <T: Any, C> handleIsIn(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, firstValue: C,
                remainingValues: Array<out C>, qualifyColumnName: Boolean) {

            val args = ArrayList<Any>(remainingValues.size + 1)
            args.add(getAppropriateArg(column, firstValue))
            remainingValues.forEach { args.add(getAppropriateArg(column, it)) }

            sections.add(ArgAwareWhereSection(
                    MultiArgPredicate(
                            MultiArgPredicate.Type.IS_IN,
                            column.getAppropriateName(qualifyColumnName),
                            args.size
                    ),

                    args
            ))
        }


        private fun <T: Any, C> handleIsNotIn(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>, firstValue: C,
                remainingValues: Array<out C>, qualifyColumnName: Boolean) {

            val args = ArrayList<Any>(remainingValues.size + 1)
            args.add(getAppropriateArg(column, firstValue))
            remainingValues.forEach { args.add(getAppropriateArg(column, it)) }

            sections.add(ArgAwareWhereSection(
                    MultiArgPredicate(
                            MultiArgPredicate.Type.IS_NOT_IN,
                            column.getAppropriateName(qualifyColumnName),
                            args.size
                    ),

                    args
            ))
        }


        private fun <T: Any, C> handleIsNull(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    NoArgPredicate(
                            NoArgPredicate.Type.IS_NULL,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    emptyList()
            ))
        }


        private fun <T: Any, C> handleIsNotNull(
                sections: MutableList<ArgAwareWhereSection>, column: Column<T, C>,
                qualifyColumnName: Boolean) {

            sections.add(ArgAwareWhereSection(
                    NoArgPredicate(
                            NoArgPredicate.Type.IS_NOT_NULL,
                            column.getAppropriateName(qualifyColumnName)
                    ),

                    emptyList()
            ))
        }


        private fun handleAnd(
                sections: MutableList<ArgAwareWhereSection>, predicate: InnerAdder.() -> Any?,
                qualifyColumnNames: Boolean) {

            val innerAdder = InnerAdderImpl(qualifyColumnNames)
            innerAdder.predicate()
            val innerAdderSections = innerAdder.sections
            throwIfSectionsIfEmpty(
                    innerAdderSections, "At least 1 predicate should be added inside and{}"
            )

            sections.add(ArgAwareWhereSection(
                    CompoundConnector(
                            CompoundConnector.Type.AND,
                            ListBackedList(innerAdderSections, transformerForList)
                    ), IterablesBackedIterable(innerAdderSections)
            ))
        }


        private fun handleOr(
                sections: MutableList<ArgAwareWhereSection>, predicate: InnerAdder.() -> Any?,
                qualifyColumnNames: Boolean) {

            val innerAdder = InnerAdderImpl(qualifyColumnNames)
            innerAdder.predicate()
            val innerAdderSections = innerAdder.sections
            throwIfSectionsIfEmpty(
                    innerAdderSections, "At least 1 predicate should be added inside or{}"
            )

            sections.add(ArgAwareWhereSection(
                    CompoundConnector(
                            CompoundConnector.Type.OR,
                            ListBackedList(innerAdderSections, transformerForList)
                    ), IterablesBackedIterable(innerAdderSections)
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