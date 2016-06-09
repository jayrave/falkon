package com.jayrave.falkon.dao.where

import com.jayrave.falkon.Column

internal sealed class WhereSection<T : Any> {

    internal sealed class Predicate<T : Any> : WhereSection<T>() {

        internal class NoArgPredicate<T : Any, C>(val type: Type, val column: Column<T, C>) :
                Predicate<T>() {

            internal enum class Type {
                IS_NULL,
                IS_NOT_NULL
            }
        }


        internal class OneArgPredicate<T : Any, C>(
                val type: Type, val column: Column<T, C>, val value: C) :
                Predicate<T>() {

            internal enum class Type {
                EQ,
                NOT_EQ,
                GREATER_THAN,
                GREATER_THAN_OR_EQ,
                LESS_THAN,
                LESS_THAN_OR_EQ
            }
        }


        internal class BetweenPredicate<T : Any, C>(
                val column: Column<T, C>, val low: C, val high: C
        ) : Predicate<T>()


        internal class LikePredicate<T : Any, C>(
                val column: Column<T, C>, val pattern: String
        ) : Predicate<T>()
    }



    internal sealed class Connector<T : Any> : WhereSection<T>() {

        internal class SimpleConnector<T : Any>(val type: Type) : Connector<T>() {
            internal enum class Type {
                AND,
                OR
            }
        }


        internal class CompoundConnector<T : Any>(
                val type: Type, val sections: List<WhereSection<T>>) :
                Connector<T>() {

            internal enum class Type {
                AND,
                OR
            }
        }
    }
}