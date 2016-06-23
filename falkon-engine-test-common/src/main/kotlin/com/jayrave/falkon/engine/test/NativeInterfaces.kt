package com.jayrave.falkon.engine.test

import com.jayrave.falkon.engine.Source

/**
 * For all the interfaces in this file, execute the required operation using
 * methods native to the platform you are writing the engine for. Eg., for
 * JDBC engine's tests, these statements should be directly executed on the
 * connections rather than via the engine
 */


/**
 * Execute the given SQL
 */
interface NativeSqlExecutor {
    fun execute(sql: String)
}


/**
 * Execute the given query
 */
interface NativeQueryExecutor {

    /**
     * Even though [Source] is asked to be returned, make sure to execute the query
     * using native methods
     */
    fun execute(query: String): Source
}
