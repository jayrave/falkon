package com.jayrave.falkon.dao

import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.executeAndClose

// ------------------------------------------ Insert -----------------------------------------------

/**
 * @return `true` if the insertion was successful; `false` otherwise
 */
fun <T : Any> com.jayrave.falkon.dao.insert.AdderOrEnder<T>.insert(): Boolean {
    return build().executeAndClose() == 1
}

// ------------------------------------------ Insert -----------------------------------------------


// ------------------------------------------ Update -----------------------------------------------

/**
 * @return number of rows affected by this update operation
 */
fun <T : Any> com.jayrave.falkon.dao.update.AdderOrEnder<T>.update(): Int {
    return build().executeAndClose()
}

/**
 * @return number of rows affected by this update operation
 */
fun <T : Any> com.jayrave.falkon.dao.update.PredicateAdderOrEnder<T>.update(): Int {
    return build().executeAndClose()
}

// ------------------------------------------ Update -----------------------------------------------


// ------------------------------------------ Delete -----------------------------------------------

/**
 * @return number of rows affected by this delete operation
 */
fun <T : Any> com.jayrave.falkon.dao.delete.DeleteBuilder<T>.delete(): Int {
    return build().executeAndClose()
}

/**
 * @return number of rows affected by this delete operation
 */
fun <T : Any> com.jayrave.falkon.dao.delete.AdderOrEnder<T>.delete(): Int {
    return build().executeAndClose()
}

// ------------------------------------------ Delete -----------------------------------------------


// ------------------------------------------ Query ------------------------------------------------

/**
 * @return [Source] that holds the result set satisfying the given conditions
 */
fun <T : Any, Z : com.jayrave.falkon.dao.query.AdderOrEnder<T, Z>>
        com.jayrave.falkon.dao.query.AdderOrEnder<T, Z>.query(): Source {
    return build().executeAndClose()
}

/**
 * @return [Source] that holds the result set satisfying the given conditions
 */
fun <T : Any> com.jayrave.falkon.dao.query.PredicateAdderOrEnder<T>.query(): Source {
    return build().executeAndClose()
}

// ------------------------------------------ Query ------------------------------------------------