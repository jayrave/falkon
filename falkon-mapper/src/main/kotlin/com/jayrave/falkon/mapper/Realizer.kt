package com.jayrave.falkon.mapper

/**
 * Used to realize instances of classes that don't map exactly to a table. For eg., this could
 * be used to realize instances of classes that carry
 *
 *  - columns from multiple tables
 *  - subset of columns from a single table
 *  - columns that include the result of running aggregate functions etc
 */
interface Realizer<out T : Any> {

    /**
     * @return An instance of [T] realized with values extracted out of [Value]
     */
    fun realize(value: Value): T


    interface Value {

        /**
         * Implementation should give back the value corresponding to the
         * passed in [column]
         */
        infix fun <C> of(column: ReadOnlyColumn<C>): C
    }
}