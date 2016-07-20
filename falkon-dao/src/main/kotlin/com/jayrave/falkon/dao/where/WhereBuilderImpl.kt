package com.jayrave.falkon.dao.where

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.dao.where.lenient.AdderOrEnder as LenientAdderOrEnder
import com.jayrave.falkon.dao.where.lenient.AfterSimpleConnectorAdder as LenientAfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.lenient.InnerAdder as LenientInnerAdder
import com.jayrave.falkon.dao.where.lenient.SimpleConnectorAdder as LenientSimpleConnectorAdder
import com.jayrave.falkon.dao.where.lenient.WhereBuilderImpl as LenientWhereBuilderImpl

/**
 * [Z] instantiated by the passed in creator function must call through (for [AdderOrEnder]
 * methods) to the instance of [AdderOrEnder] provided while invoking the creator
 */
internal class WhereBuilderImpl<T : Any, Z : AdderOrEnder<T, Z>>(
        adderOrEnderCreator: (AdderOrEnder<T, Z>) -> Z) :
        WhereBuilder<T, Z>,
        AdderOrEnder<T, Z>,
        AfterSimpleConnectorAdder<T, Z> {

    private val adderOrEnder: Z by lazy { adderOrEnderCreator.invoke(this) }
    private val lenientWhereBuilder =
            LenientWhereBuilderImpl<AdderOrEnderForLenientBuilder>(false) {
                AdderOrEnderForLenientBuilder(it)
            }


    internal fun build(): Where = lenientWhereBuilder.build()


    override fun <C> eq(column: Column<T, C>, value: C): Z {
        lenientWhereBuilder.eq(column, value)
        return adderOrEnder
    }


    override fun <C> notEq(column: Column<T, C>, value: C): Z {
        lenientWhereBuilder.notEq(column, value)
        return adderOrEnder
    }


    override fun <C> gt(column: Column<T, C>, value: C): Z {
        lenientWhereBuilder.gt(column, value)
        return adderOrEnder
    }


    override fun <C> ge(column: Column<T, C>, value: C): Z {
        lenientWhereBuilder.ge(column, value)
        return adderOrEnder
    }


    override fun <C> lt(column: Column<T, C>, value: C): Z {
        lenientWhereBuilder.lt(column, value)
        return adderOrEnder
    }


    override fun <C> le(column: Column<T, C>, value: C): Z {
        lenientWhereBuilder.le(column, value)
        return adderOrEnder
    }


    override fun <C> between(column: Column<T, C>, low: C, high: C): Z {
        lenientWhereBuilder.between(column, low, high)
        return adderOrEnder
    }


    override fun <C> like(column: Column<T, C>, pattern: String): Z {
        lenientWhereBuilder.like(column, pattern)
        return adderOrEnder
    }


    override fun <C> isIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z {
        lenientWhereBuilder.isIn(column, firstValue, *remainingValues)
        return adderOrEnder
    }


    override fun <C> isNotIn(column: Column<T, C>, firstValue: C, vararg remainingValues: C): Z {
        lenientWhereBuilder.isNotIn(column, firstValue, *remainingValues)
        return adderOrEnder
    }


    override fun <C> isNull(column: Column<T, C>): Z {
        lenientWhereBuilder.isNull(column)
        return adderOrEnder
    }


    override fun <C> isNotNull(column: Column<T, C>): Z {
        lenientWhereBuilder.isNotNull(column)
        return adderOrEnder
    }


    override fun and(predicate: InnerAdder<T>.() -> Any?): Z {
        lenientWhereBuilder.and() { InnerAdderImpl<T>(this).predicate() }
        return adderOrEnder
    }


    override fun or(predicate: InnerAdder<T>.() -> Any?): Z {
        lenientWhereBuilder.or() { InnerAdderImpl<T>(this).predicate() }
        return adderOrEnder
    }


    override fun and(): AfterSimpleConnectorAdder<T, Z> {
        lenientWhereBuilder.and()
        return this
    }


    override fun or(): AfterSimpleConnectorAdder<T, Z> {
        lenientWhereBuilder.or()
        return this
    }



    private inner class AdderOrEnderForLenientBuilder(
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