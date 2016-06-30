package com.jayrave.falkon.engine

/**
 *  - Every [Type] will be bound by calling the appropriate `bind` method on [CompiledStatement]
 *  - [TypedNull] will be bound by calling the appropriate bind method
 *  - Every other argument will be bound as [String]
 *
 * @param index the 1-based index where [value] will be bound
 * @param value the parameter value
 */
fun <CS: CompiledStatement<R>, R> CS.bind(index: Int, value: Any): CS {
    when (value) {
        is Short -> bindShort(index, value)
        is Int -> bindInt(index, value)
        is Long -> bindLong(index, value)
        is Float -> bindFloat(index, value)
        is Double -> bindDouble(index, value)
        is ByteArray -> bindBlob(index, value)
        is TypedNull -> bindNull(index, value.type)
        else -> bindString(index, value.toString())
    }

    return this
}


/**
 * All arguments will be bound according to the semantics of [bind]
 *
 * @param values to be bound
 * @param startIndex the 1-based index from where binding should be started
 */
fun <CS: CompiledStatement<R>, R> CS.bindAll(values: Iterable<Any>?, startIndex: Int = 1): CS {
    values?.forEachIndexed { index, value ->
        bind(startIndex + index, value)
    }

    return this
}


/**
 * Executes the given operation. Closes the statement if the operation throws
 */
inline fun <CS : CompiledStatement<R>, R, Z> CS.closeIfOpThrows(operation: CS.() -> Z): Z {
    try {
        return operation.invoke(this)
    } catch (t: Throwable) {
        try {
            close()
        } catch (e: Exception) {
            // Don't let any exception while closing the statement to
            // hide the original throwable that was thrown
        }

        throw t
    }
}