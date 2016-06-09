package com.jayrave.falkon.testLib

import org.mockito.ArgumentMatcher

/**
 * Check whether the expected & actual iterables have the same length and have equal elements.
 * Equality check semantics are the same as that of a [Set]
 */
class IterableContainsInAnyOrder<T : Comparable<T>>(private val expected: Iterable<T>) :
        ArgumentMatcher<Iterable<T>> {

    override fun matches(argument: Any?): Boolean {
        return when (argument) {
            !is Iterable<*> -> {
                printErr("expected iterable: ${toString()}; actual: ${argument.toString()}")
                false
            }

            else -> {
                @Suppress("UNCHECKED_CAST")
                val actual = argument as Iterable<T>
                val result = expected.toSet() == actual.toSet()
                if (!result) {
                    printErr("expected: ${toString()}; actual: ${actual.toFormattedString()}")
                }

                result
            }
        }
    }

    override fun toString(): String {
        return expected.toFormattedString()
    }


    companion object {
        private fun printErr(error: String) {
            System.err.println(error)
        }


        private fun Iterable<Any?>.toFormattedString(): String {
            return joinToString(separator = ", ", prefix = "[", postfix = "]")
        }
    }
}