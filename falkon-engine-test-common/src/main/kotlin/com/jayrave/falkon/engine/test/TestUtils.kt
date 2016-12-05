package com.jayrave.falkon.engine.test

import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import kotlin.reflect.KClass

/**
 * Executes [op] & if it doesn't throw an exception of instance [expectedExceptionClass],
 * an [AssertionError] will be thrown
 */
internal fun failIfOpDoesNotThrow(
        expectedExceptionClass: KClass<out Exception> = Exception::class,
        op: () -> Any?) {

    try {
        op.invoke()
        failBecauseExceptionWasNotThrown(expectedExceptionClass.java)
    } catch (e: Exception) {
        when {
            expectedExceptionClass.java.isInstance(e) -> { /* Expected expected. Just swallow */ }
            else -> throw e
        }
    }
}