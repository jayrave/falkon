package com.jayrave.falkon.dao

/**
 * Executes the passed in [operation] & closes the passed in [AutoCloseable] safely (even if
 * an exception is thrown)
 */
internal inline fun <AC : AutoCloseable, R> AC.safeCloseAfterOp(operation: AC.() -> R): R {
    try {
        return operation.invoke(this)
    } finally {
        close()
    }
}