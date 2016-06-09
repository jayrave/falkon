package com.jayrave.falkon.dao.where

import com.jayrave.falkon.Column
import com.jayrave.falkon.dao.where.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.dao.where.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.dao.where.WhereSection.Predicate.*
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

    private val sections: MutableList<WhereSection<T>> = LinkedList()
    private val adderOrEnder: Z by lazy { adderOrEnderCreator.invoke(this) }

    internal fun build(): Where {
        return sections.buildWhere()
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
        sections.add(SimpleConnector(SimpleConnector.Type.AND))
        return this
    }


    override fun or(): AfterSimpleConnectorAdder<T, Z> {
        sections.add(SimpleConnector(SimpleConnector.Type.OR))
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

        val sections: MutableList<WhereSection<T>> = LinkedList()

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



    companion object {

        private fun <T: Any, C> handleEq(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, value: C) {

            sections.add(OneArgPredicate(OneArgPredicate.Type.EQ, column, value))
        }


        private fun <T: Any, C> handleNotEq(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, value: C) {

            sections.add(OneArgPredicate(OneArgPredicate.Type.NOT_EQ, column, value))
        }


        private fun <T: Any, C> handleGt(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, value: C) {

            sections.add(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, column, value))
        }


        private fun <T: Any, C> handleGe(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, value: C) {

            sections.add(OneArgPredicate(OneArgPredicate.Type.GREATER_THAN_OR_EQ, column, value))
        }


        private fun <T: Any, C> handleLt(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, value: C) {

            sections.add(OneArgPredicate(OneArgPredicate.Type.LESS_THAN, column, value))
        }


        private fun <T: Any, C> handleLe(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, value: C) {

            sections.add(OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, column, value))
        }


        private fun <T: Any, C> handleBetween(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, low: C, high: C) {

            sections.add(BetweenPredicate(column, low, high))
        }


        private fun <T: Any, C> handleLike(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>, pattern: String) {

            sections.add(LikePredicate(column, pattern))
        }


        private fun <T: Any, C> handleIsNull(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>) {

            sections.add(NoArgPredicate(NoArgPredicate.Type.IS_NULL, column))
        }


        private fun <T: Any, C> handleIsNotNull(
                sections: MutableList<WhereSection<T>>, column: Column<T, C>) {

            sections.add(NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, column))
        }


        private fun <T: Any> handleAnd(
                sections: MutableList<WhereSection<T>>, predicate: InnerAdder<T>.() -> Any?) {

            val innerAdder = InnerAdderImpl<T>()
            innerAdder.predicate()
            val addedSections = innerAdder.sections
            throwIfSectionsIfEmpty(
                    addedSections, "At least 1 predicate should be added inside and{}"
            )

            sections.add(CompoundConnector(CompoundConnector.Type.AND, addedSections))
        }


        private fun <T: Any> handleOr(
                sections: MutableList<WhereSection<T>>, predicate: InnerAdder<T>.() -> Any?) {

            val innerAdder = InnerAdderImpl<T>()
            innerAdder.predicate()
            val addedSections = innerAdder.sections
            throwIfSectionsIfEmpty(
                    addedSections, "At least 1 predicate should be added inside or{}"
            )

            sections.add(CompoundConnector(CompoundConnector.Type.OR, addedSections))
        }


        private fun <T: Any> throwIfSectionsIfEmpty(
                sections: List<WhereSection<T>>, error: String) {

            if (sections.isEmpty()) {
                throw SQLSyntaxErrorException(error)
            }
        }
    }
}