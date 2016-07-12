package com.jayrave.falkon.lib

/**
 * Executes the passed in [operation] & closes the passed in [AutoCloseable] safely (even if
 * an exception is thrown)
 */
inline fun <AC : AutoCloseable, R> AC.safeCloseAfterOp(operation: AC.() -> R): R {
    try {
        return operation.invoke(this)
    } finally {
        close()
    }
}