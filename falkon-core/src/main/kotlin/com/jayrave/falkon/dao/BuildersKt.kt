package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.CompiledStatement

// ------------------------------------------ Insert -----------------------------------------------

/**
 * @return `true` if the insertion was successful; `false` otherwise
 */
fun <T : Any> com.jayrave.falkon.dao.insert.AdderOrEnder<T>.insert(): Boolean {
    return build().safeCloseAfterExecution() == 1
}

// ------------------------------------------ Insert -----------------------------------------------


// ------------------------------------------ Update -----------------------------------------------

/**
 * @return number of rows affected by this update operation
 */
fun <T : Any> com.jayrave.falkon.dao.update.AdderOrEnder<T>.update(): Int {
    return build().safeCloseAfterExecution()
}

/**
 * @return number of rows affected by this update operation
 */
fun <T : Any> com.jayrave.falkon.dao.update.PredicateAdderOrEnder<T>.update(): Int {
    return build().safeCloseAfterExecution()
}

// ------------------------------------------ Update -----------------------------------------------


// ------------------------------------------ Delete -----------------------------------------------

/**
 * @return number of rows affected by this delete operation
 */
fun <T : Any> com.jayrave.falkon.dao.delete.DeleteBuilder<T>.delete(): Int {
    return build().safeCloseAfterExecution()
}

/**
 * @return number of rows affected by this delete operation
 */
fun <T : Any> com.jayrave.falkon.dao.delete.AdderOrEnder<T>.delete(): Int {
    return build().safeCloseAfterExecution()
}

// ------------------------------------------ Delete -----------------------------------------------


// A #query convenience function is not included here as it doesn't make sense to. Source that is
// returned from a CompiledQuery could end up not working if the CompiledQuery itself is closed


/**
 * Executes the statement, closes it (no matter if exception is thrown or not) and
 * returns the result of execution
 */
fun <R> CompiledStatement<R>.safeCloseAfterExecution(): R {
    try {
        return execute()
    } finally {
        close()
    }
}