package com.jayrave.falkon.sqlBuilders.query

/**
 * Represents parts of a SQL WHERE clause
 */
sealed class WhereSection {

    /**
     * Used to specify the conditions which is used to filter the data in the table
     */
    sealed class Predicate : WhereSection() {

        /**
         * Represents conditions that just required a column name to work on.
         * Eg., `column_1 IS NULL`
         */
        class NoArgPredicate(val type: Type, val columnName: String) : Predicate() {
            enum class Type {
                IS_NULL,
                IS_NOT_NULL
            }
        }


        /**
         * Represents conditions that work on the value of a column. Eg., `column_1 >= 5`
         */
        class OneArgPredicate(val type: Type, val columnName: String) : Predicate() {
            enum class Type {
                EQ,
                NOT_EQ,
                GREATER_THAN,
                GREATER_THAN_OR_EQ,
                LESS_THAN,
                LESS_THAN_OR_EQ,
                LIKE
            }
        }


        /**
         * Represents conditions that compare the value in a column against a list of values.
         * Eg., `column_1 in 5, 6`
         */
        class MultiArgPredicate(val type: Type, val columnName: String, val numberOfArgs: Int) :
                Predicate() {

            enum class Type {
                IS_IN,
                IS_NOT_IN
            }
        }


        /**
         * Represents BETWEEN condition that takes in 2 values => low and high. This is usually
         * of the form `column_name BETWEEN low_value AND high_value`
         */
        class BetweenPredicate(val columnName: String) : Predicate()
    }



    /**
     * Used to connect [Predicate]s
     */
    sealed class Connector : WhereSection() {

        /**
         * Represents a simple AND or OR used to connect two [Predicate]s. This is usually of
         * the form `predicate_1 AND predicate_2`
         */
        class SimpleConnector(val type: Type) : Connector() {
            enum class Type {
                AND,
                OR
            }
        }


        /**
         * Represents a AND or OR used to connect two or more [Predicate]s or other
         * [CompoundConnector]s. This is usually of the form
         * `(predicate_1 AND predicate_2 AND ....... AND predicate_n)`
         */
        class CompoundConnector(val type: Type, val sections: List<WhereSection>) : Connector() {
            enum class Type {
                AND,
                OR
            }
        }
    }
}