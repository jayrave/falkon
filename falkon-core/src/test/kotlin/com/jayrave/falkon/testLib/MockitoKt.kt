package com.jayrave.falkon.testLib

import org.mockito.Mockito

inline fun <reified T : Comparable<T>> iterableContainsInAnyOrder(vararg expected: T):
        Iterable<T> {
    return iterableContainsInAnyOrder(expected.asList())
}


inline fun <reified T : Comparable<T>> iterableContainsInAnyOrder(expected: Iterable<T>):
        Iterable<T> {
    Mockito.argThat(IterableContainsInAnyOrder(expected))
    return expected
}
