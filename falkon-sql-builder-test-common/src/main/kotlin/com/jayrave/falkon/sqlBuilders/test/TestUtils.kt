package com.jayrave.falkon.sqlBuilders.test

import java.util.*
import kotlin.reflect.KClass

/**
 * Creates a random [UUID]
 */
fun randomUuid(): UUID = UUID.randomUUID()


/**
 * Builds an argument list to be used in SQL statements. `null` & numbers are not quoted.
 * Every other type is stringified & quoted
 */
fun buildArgListForSql(vararg args: Any?): String {
    return args.joinToString() {
        when (it) {
            null -> "null"
            is Number -> it.toString()
            else -> "'$it'"
        }
    }
}


/**
 * Executes [op] & if it doesn't throw an exception of instance [expectedExceptionClass],
 * an [AssertionError] will be thrown
 */
fun failIfOpDoesNotThrow(
        expectedExceptionClass: KClass<out Exception> = Exception::class, op: () -> Any?) {

    var caughtException: Exception? = null
    try {
        op.invoke()
    } catch (e: Exception) {
        caughtException = when {
            expectedExceptionClass.java.isInstance(e) -> e
            else -> throw e
        }
    }

    assert(caughtException != null) {
        "Expected exception not thrown: ${expectedExceptionClass.java.canonicalName}"
    }
}