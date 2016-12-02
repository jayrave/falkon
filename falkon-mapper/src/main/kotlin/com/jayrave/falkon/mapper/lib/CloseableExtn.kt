package com.jayrave.falkon.mapper.lib

import java.io.Closeable

/**
 * Executes the passed in [operation] & closes the passed in [Closeable] safely (even if
 * an exception is thrown)
 */
inline fun <C : Closeable, R> C.safeCloseAfterOp(operation: C.() -> R): R {
    try {
        return operation.invoke(this)
    } finally {
        close()
    }
}