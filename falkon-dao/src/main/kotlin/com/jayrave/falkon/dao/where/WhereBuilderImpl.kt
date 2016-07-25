package com.jayrave.falkon.dao.where

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.dao.where.lenient.AdderOrEnder as LenientAdderOrEnder
import com.jayrave.falkon.dao.where.lenient.InnerAdder as LenientInnerAdder
import com.jayrave.falkon.dao.where.lenient.WhereBuilderImpl as LenientWhereBuilderImpl

/**
 * [Z] instantiated by the passed in creator function must call through (for [AdderOrEnder]
 * methods) to the instance of [AdderOrEnder] provided while invoking the creator
 *
 * *Note:* By default, column names are not qualified. If it is desired, you will have to
 * inject in a custom [LenientWhereBuilderImpl]
 *
 * @param implementation that should be used under the hood for the actual working.
 * If it is `null`, one will created & used
 */
internal class WhereBuilderImpl<T : Any, Z : AdderOrEnder<T, Z>>(
        adderOrEnderCreator: (AdderOrEnder<T, Z>) -> Z,
        implementation: LenientWhereBuilderImpl<*>? = null) :
        WhereBuilder<T, Z>,
        AdderOrEnder<T, Z>,
        AfterSimpleConnectorAdder<T, Z> {

    private val adderOrEnder: Z by lazy { adderOrEnderCreator.invoke(this) }
    private val lenientWhereBuilderImpl: LenientWhereBuilderImpl<*> =
            implementation ?: LenientWhereBuilderImpl<AdderOrEnderForLenientBuilder>(false) {
                AdderOrEnderForLenientBuilder(it)
            }


    internal fun build(): Where = lenientWhereBuilderImpl.build()


    override fun <C> eq(column: Column<T, C>, value: C): Z {
        lenientWhereBuilderImpl.eq(column, value)
        return adderOrEnder
    }


    override fun <C> notEq(column: Column<T, C>, value: C): Z {
        lenientWhereBuilderImpl.notEq(column, value)
        return adderOrEnder
    }


    override fun <C> gt(column: Column<T, C>, value: C): Z {
        lenientWhereBuilderImpl.gt(column, value)
        return adderOrEnder
    }


    override fun <C> ge(column: Column<T, C>, value: C): Z {
        lenientWhereBuilderImpl.ge(column, value)
        return adderOrEnder
    }


    override fun <C> lt(column: Column<T, C>, value: C): Z {
        lenientWhereBuilderImpl.lt(column, value)
        return adderOrEnder
    }


    override fun <C> le(column: Column<T, C>, value: C): Z {
        lenientWhereBuilderImpl.le(column, value)
        return adderOrEnder
    }


    override fun <C> between(column: Column<T, C>, low: C, high: C): Z {
        lenientWhereBuilderImpl.between(column, low, high)
        return adderOrEnder
    }


    override fun <C> like(column: Column<T, C>, pattern: String): Z {
        lenientWhereBuilderImpl.like(column, pattern)
        return adderOrEnder
    }


    override fun <C> isIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z {
        lenientWhereBuilderImpl.isIn(column, firstValue, *remainingValues)
        return adderOrEnder
    }


    override fun <C> isNotIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z {
        lenientWhereBuilderImpl.isNotIn(column, firstValue, *remainingValues)
        return adderOrEnder
    }


    override fun <C> isNull(column: Column<T, C>): Z {
        lenientWhereBuilderImpl.isNull(column)
        return adderOrEnder
    }


    override fun <C> isNotNull(column: Column<T, C>): Z {
        lenientWhereBuilderImpl.isNotNull(column)
        return adderOrEnder
    }


    override fun and(predicate: InnerAdder<T>.() -> Any?): Z {
        lenientWhereBuilderImpl.and() { InnerAdderImpl<T>(this).predicate() }
        return adderOrEnder
    }


    override fun or(predicate: InnerAdder<T>.() -> Any?): Z {
        lenientWhereBuilderImpl.or() { InnerAdderImpl<T>(this).predicate() }
        return adderOrEnder
    }


    override fun and(): AfterSimpleConnectorAdder<T, Z> {
        lenientWhereBuilderImpl.and()
        return this
    }


    override fun or(): AfterSimpleConnectorAdder<T, Z> {
        lenientWhereBuilderImpl.or()
        return this
    }



    private class AdderOrEnderForLenientBuilder(
            private val delegate: LenientAdderOrEnder<AdderOrEnderForLenientBuilder>) :
            LenientAdderOrEnder<AdderOrEnderForLenientBuilder> {

        override fun and() = delegate.and()
        override fun or() = delegate.or()
    }



    private class InnerAdderImpl<T : Any>(private val lenientInnerAdder: LenientInnerAdder) :
            InnerAdder<T> {

        override fun <C> eq(column: Column<T, C>, value: C) {
            lenientInnerAdder.eq(column, value)
        }

        override fun <C> notEq(column: Column<T, C>, value: C) {
            lenientInnerAdder.notEq(column, value)
        }

        override fun <C> gt(column: Column<T, C>, value: C) {
            lenientInnerAdder.gt(column, value)
        }

        override fun <C> ge(column: Column<T, C>, value: C) {
            lenientInnerAdder.ge(column, value)
        }

        override fun <C> lt(column: Column<T, C>, value: C) {
            lenientInnerAdder.lt(column, value)
        }

        override fun <C> le(column: Column<T, C>, value: C) {
            lenientInnerAdder.le(column, value)
        }

        override fun <C> between(column: Column<T, C>, low: C, high: C) {
            lenientInnerAdder.between(column, low, high)
        }

        override fun <C> like(column: Column<T, C>, pattern: String) {
            lenientInnerAdder.like(column, pattern)
        }

        override fun <C> isIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C) {
            lenientInnerAdder.isIn(column, firstValue, *remainingValues)
        }

        override fun <C> isNotIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C) {
            lenientInnerAdder.isNotIn(column, firstValue, *remainingValues)
        }

        override fun <C> isNull(column: Column<T, C>) {
            lenientInnerAdder.isNull(column)
        }

        override fun <C> isNotNull(column: Column<T, C>) {
            lenientInnerAdder.isNotNull(column)
        }

        override fun and(predicate: InnerAdder<T>.() -> Any?) {
            lenientInnerAdder.and() { InnerAdderImpl<T>(this).predicate() }
        }

        override fun or(predicate: InnerAdder<T>.() -> Any?) {
            lenientInnerAdder.or() { InnerAdderImpl<T>(this).predicate() }
        }
    }
}